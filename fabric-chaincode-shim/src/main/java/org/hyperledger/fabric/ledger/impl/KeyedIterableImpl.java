/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger.impl;

import static org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage.Type.GET_STATE_BY_RANGE;
import static org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage.Type.QUERY_STATE_CLOSE;

import java.util.Collections;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.BiFunction;

import org.hyperledger.fabric.contract.ContractRuntimeException;
import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.State;
import org.hyperledger.fabric.protos.ledger.queryresult.KvQueryResult.KV;
import org.hyperledger.fabric.protos.peer.ChaincodeShim;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.ChaincodeMessage.Type;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.GetStateByRange;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryResponse;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryResultBytes;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryStateClose;
import org.hyperledger.fabric.protos.peer.ChaincodeShim.QueryStateNext;
import org.hyperledger.fabric.shim.ChaincodeSpi;

import com.google.protobuf.ByteString;
import com.google.protobuf.InvalidProtocolBufferException;

public class KeyedIterableImpl implements Iterable<State>, CollectionIterable<State> {

	private Iterator<QueryResultBytes> currentIterator;
	private QueryResponse currentQueryResponse;
	ChaincodeShim.QueryResponseMetadata currentMetadata;

	private BiFunction<ChaincodeMessage.Type, ByteString, ByteString> invoke;
	private int pageSize;
	private int fetchedCount;
	private String bookmark;
	private String startKey;
	private String endKey;
	private String collection;

	public KeyedIterableImpl(String collection, String startKey, String endKey, ChaincodeSpi spi) {
		this.invoke = spi::invokeRequest;
		this.collection = collection;
		this.startKey = startKey;
		this.endKey = endKey;
		this.pageSize = 0;
		this.bookmark = null;
	}

	public KeyedIterableImpl(String collection, String startKey, String endKey, int pageSize, String bookmark,
			ChaincodeSpi spi) {
		this.invoke = spi::invokeRequest;
		this.collection = collection;
		this.startKey = startKey;
		this.endKey = endKey;
		this.pageSize = pageSize;
		this.bookmark = bookmark;
	}

	public CollectionIterable<State> setPagingSize(int pageSize) {
		this.pageSize = pageSize;
		return this;
	}

	@Override
	public int getFetchedCount() {
		return fetchedCount;
	}

	@Override
	public void close() throws ContractRuntimeException {
		ByteString requestPayload = QueryStateClose.newBuilder().setId(currentQueryResponse.getId()).build()
				.toByteString();

		this.invoke.apply(QUERY_STATE_CLOSE, requestPayload);

		currentIterator = Collections.emptyIterator();
		currentQueryResponse = QueryResponse.newBuilder().setHasMore(false).build();
	}

	private void updateParsedResponse(ByteString data) {
		try {
			this.currentQueryResponse = QueryResponse.parseFrom(data);
			ChaincodeShim.QueryResponseMetadata metadata = ChaincodeShim.QueryResponseMetadata
					.parseFrom(currentQueryResponse.getMetadata());

			this.bookmark = metadata.getBookmark();
			this.fetchedCount = metadata.getFetchedRecordsCount();

		} catch (InvalidProtocolBufferException e) {
			throw new RuntimeException(e);
		}

	}

	private ByteString nextState() {
		ByteString requestPayload = QueryStateNext.newBuilder().setId(currentQueryResponse.getId()).build()
				.toByteString();
		return this.invoke.apply(Type.QUERY_STATE_NEXT, requestPayload);
	}

	@Override
	public Iterator<State> iterator() {

		// send the initial query to the peer
		ChaincodeShim.QueryMetadata queryMetadata = ChaincodeShim.QueryMetadata.newBuilder().setBookmark(bookmark)
				.setPageSize(pageSize).build();

		ByteString initialRequest = GetStateByRange.newBuilder().setCollection(collection).setStartKey(startKey)
				.setEndKey(endKey).setMetadata(queryMetadata.toByteString()).build().toByteString();

		// parse the response
		updateParsedResponse(invoke.apply(GET_STATE_BY_RANGE, initialRequest));

		// return the iterator over those bits
		return new Iterator<State>() {

			@Override
			public boolean hasNext() {
				return currentIterator.hasNext() || currentQueryResponse.getHasMore();
			}

			@Override
			public State next() {

				QueryResultBytes resultBytes = null;
				// return next fetched result, if any
				if (!currentIterator.hasNext()) {

					// throw exception if there are no more expected results
					if (!currentQueryResponse.getHasMore()) {
						throw new NoSuchElementException();
					}

					updateParsedResponse(nextState());
					currentIterator = currentQueryResponse.getResultsList().iterator();

				}

				// return next fetched result
				resultBytes = currentIterator.next();
				KV keyvalue;
				try {
					keyvalue = KV.parseFrom(resultBytes.getResultBytes());
				} catch (InvalidProtocolBufferException e) {
					throw new RuntimeException(e);
				}
				return new StateImpl(keyvalue);

			}

		};

	}

	@Override
	public CollectionIterable<State> setBookMark(String bookmark) {
		this.bookmark = bookmark;
		return this;
	}

	@Override
	public String getBookmark() {
		return bookmark;
	}

}
