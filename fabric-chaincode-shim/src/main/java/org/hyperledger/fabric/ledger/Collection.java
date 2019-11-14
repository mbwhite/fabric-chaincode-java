/*
SPDX-License-Identifier: Apache-2.0
*/
package org.hyperledger.fabric.ledger;

import org.hyperledger.fabric.FuturePrototype;
import org.hyperledger.fabric.contract.ContractRuntimeException;
import org.hyperledger.fabric.contract.annotation.DataType;

/**
 * 
 *
 */
public interface Collection  {
	
	/**
	 * Constant that can be used to refer to the 'Worldstate' collection explicitly
	 */
    String WORLD = "worldstate";
    
    /**
     * Given the mspid, return the name of the implicit collection 
     * for the organization.
     * 
     * @param mspid String organization's mspid
     * @return Collection name
     */
    static String getOrganizationCollectionName(String mspid) {
    	return "";
    }

	/**
     * Returns the value of the specified <code>key</code> from the ledger.
     * <p>
     * Note that getState doesn't read data from the writeset, which has not been committed to the ledger.
     * In other words, GetState doesn't consider data modified by PutState that has not been committed.
     *
     * @param key name of the value
     * @return value the value read from the ledger
     */
    State getState(String key);
       
    /**
     * Creates the specified <code>key</code> and <code>value</code> into the transaction's
     * read and write-set as a data-write proposal.
     * <p>
     * If the key does exist already then an exception is thrown. 
     * <p>
     * putState doesn't effect the ledger
     * until the transaction is validated and successfully committed.
     * Simple keys must not be an empty string and must not start with 0x00
     * character, in order to avoid range query collisions with
     * composite keys
     *
     * @param key   name of the value
     * @param value the value to write to the ledger
     */    
    State createState(String key, byte[] valueAsBytes);    

    /**
     * Updates the specified <code>key</code> and <code>value</code> into the transaction's
     * read and write-set as a data-write proposal.
     * <p>
     * If the key does not exist then an exception is thrown. 
     * <p>
     * putState doesn't effect the ledger
     * until the transaction is validated and successfully committed.
     * Simple keys must not be an empty string and must not start with 0x00
     * character, in order to avoid range query collisions with
     * composite keys
     *
     * @param key   name of the value
     * @param value the value to write to the ledger
     */    
    State updateState(String key, byte[] valueAsBytes);  
    
    /**
     * Records the specified <code>key</code> to be deleted in the writeset of
     * the transaction proposal.
     * <p>
     * The <code>key</code> and its value will be deleted from	
     * the ledger when the transaction is validated and successfully committed.
     *
     * @param key name of the value to be deleted
     */
    void deleteState(String key);    
    
    /** Returns an iterable based on the supplied query factory
     *  
     * @param queryProfile
     * @return
     */
    CollectionIterable<State> getStates(QueryHandlerFactory queryFactory);
        
    /**
     * The object form of this getState API would be along the following lines
     *  @DataType is an existing annotation on the Contract package
     *   
     * @param <T> Class Type of the object being 'got'
     * @param key String key (overrides any defined key in the object)
     * @param clz Class object 
     * @return Instance of the object
     */
    @FuturePrototype
    <@DataType T> T getObject(String key, Class<T> clz);   
    
    /**
     *  The object form of this createState API would be along the following lines
     *  @DataType is an existing annotation on the Contract package
     *  
     *  @param obj Object to be stored, must have the @DataType annotation
     *  @param key String key (overrides any defined key in the object)
     *  @return State object     
     */
    @FuturePrototype    
    State createState(String key, @DataType Object obj);
    @FuturePrototype    
    State updateState(String key, @DataType Object obj);
    
    /** 
     * Same query approach but with objects.
     * Assumption that the states that are returned all all the same Class
     * 
     */
    <R> CollectionIterable<R> getStates(QueryHandlerFactory queryFactory, Class<R> clz);
    
     /**
      * Conjunction of the standard Iterable interface and auto-closeable.
      * 
      * This is required as the underlying protocol demands a close to be sent.
      * If the code aborts the iteration for whatever reason this close needs to be available
      * to be called.
      * 
      * Being 'auto-closeable' means that the try-with-resources can be used to
      * increase safetly.  In addition we can ensure that the exception from close
      * is consistent with other APIs
      *
      * It also permits the paging feature to be used to allow the underlying transport
      * to batch up requests.
      *
      * @param <T>
      */
     public interface CollectionIterable<S> extends Iterable<S>, AutoCloseable{

    	 /** Sets the paging size to be used
    	  * Lets the underlying protocol stagger the requests.
    	  * 
    	  * Once the first call has been made to use this iterator, this value
    	  * can not be modified; throws IllegalStateException if so called.
    	  */
    	 CollectionIterable<S> setPagingSize(int pageSize);
    	 
    	 /** 
          * A count of the number of fetched records
          * 
    	  * @return
    	  */
    	 int getFetchedCount();
    	 
    	 /** 
    	  * Sets the book mark to be used
    	  * Once the first call has been made to use this iterator, this value
    	  * can not be modified; throws IllegalStateException if so called.
    	  * 
    	  * @param bookmark
    	  */
    	 CollectionIterable<S> setBookMark(String bookmark);
    	 
    	 /**
    	  * The current bookmark
    	  * 
    	  * @return
    	  */
    	 String getBookmark();
    	 
    	 /**
    	  * Close the iterable; use with the a try-with-resources handler will
    	  * do this automatically
    	  */
    	 void close() throws ContractRuntimeException;
     }
    
     
}
