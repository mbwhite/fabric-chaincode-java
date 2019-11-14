/*
SPDX-License-Identifier: Apache-2.0
*/
package org.example.contract;

import java.time.Instant;
import java.util.logging.Logger;

import org.example.adts.CommercialPaper;
import org.hyperledger.fabric.contract.Context;
import org.hyperledger.fabric.contract.ContractInterface;
import org.hyperledger.fabric.contract.annotation.Contact;
import org.hyperledger.fabric.contract.annotation.Contract;
import org.hyperledger.fabric.contract.annotation.Default;
import org.hyperledger.fabric.contract.annotation.Info;
import org.hyperledger.fabric.contract.annotation.License;
import org.hyperledger.fabric.contract.annotation.Transaction;
import org.hyperledger.fabric.ledger.Collection;
import org.hyperledger.fabric.ledger.Collection.CollectionIterable;
import org.hyperledger.fabric.ledger.KeyQueryHandler;
import org.hyperledger.fabric.ledger.Ledger;
import org.hyperledger.fabric.ledger.State;
import org.hyperledger.fabric.ledger.StateHistory;
import org.hyperledger.fabric.ledger.policy.Expression;
import org.hyperledger.fabric.ledger.policy.Principal;
import org.hyperledger.fabric.ledger.policy.Role;
import org.hyperledger.fabric.ledger.policy.StateBasedEndorsement;
import org.hyperledger.fabric.shim.ChaincodeStub;

/**
 * Define commercial paper smart contract by extending Fabric Contract class
 *
 */
@Contract(name = "org.papernet.commercialpaper", info = @Info(title = "MyAsset contract", description = "", version = "0.0.1", license = @License(name = "SPDX-License-Identifier: ", url = ""), contact = @Contact(email = "java-contract@example.com", name = "java-contract", url = "http://java-contract.me")))
@Default
public class CommercialPaperContract implements ContractInterface {

	private final static Logger logger = Logger.getLogger(CommercialPaperContract.class.getName());

	/**
	 * Define a custom context for commercial paper
	 */
	@Override
	public Context createContext(ChaincodeStub stub) {
		return new CommercialPaperContext(stub);
	}

	public CommercialPaperContract() {

	}

	/**
	 * Issue commercial paper
	 *
	 * @param {Context} ctx the transaction context
	 * @param {String}  issuer commercial paper issuer
	 * @param {Integer} paperNumber paper number for this issuer
	 * @param {String}  issueDateTime paper issue date
	 * @param {String}  maturityDateTime paper maturity date
	 * @param {Integer} faceValue face value of paper
	 */
	@Transaction(submit = true)
	public CommercialPaper issue(CommercialPaperContext ctx, String issuer, String paperNumber, String issueDateTime,
			String maturityDateTime, int faceValue) {

		// create an instance of the paper
		CommercialPaper paper = CommercialPaper.createInstance(issuer, paperNumber, issueDateTime, maturityDateTime,
				faceValue, issuer, "");

		// Smart contract, rather than paper, moves paper into ISSUED state
		paper.setIssued();

		// Newly issued paper is owned by the issuer
		paper.setOwner(issuer);

		// want to put this into the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);

		// put into the collection 
		String key = State.makeComposite(CommercialPaper.class.getName(), new String[] { paperNumber });
		worldCollection.putState(key, CommercialPaper.serialize(paper));
		
		return paper;
	}

	/**
	 * Buy commercial paper
	 *
	 * @param {Context} ctx the transaction context
	 * @param {String}  issuer commercial paper issuer
	 * @param {Integer} paperNumber paper number for this issuer
	 * @param {String}  currentOwner current owner of paper
	 * @param {String}  newOwner new owner of paper
	 * @param {Integer} price price paid for this paper
	 * @param {String}  purchaseDateTime time paper was purchased (i.e. traded)
	 */
	@Transaction(submit = true)
	public CommercialPaper buy(CommercialPaperContext ctx, String issuer, String paperNumber, String currentOwner,
			String newOwner, int price, String purchaseDateTime) {

		// want to put this into the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);

		// get the state
		State paperState = worldCollection.getState(paperNumber);

		// and then to the Commercial Paper
		CommercialPaper paper = CommercialPaper.deserialize(paperState.getValue());

		// Business logic
		// Validate current owner
		if (!paper.getOwner().equals(currentOwner)) {
			throw new RuntimeException("Paper " + issuer + paperNumber + " is not owned by " + currentOwner);
		}

		// First buy moves state from ISSUED to TRADING
		if (paper.isIssued()) {
			paper.setTrading();
		}

		// Check paper is not already REDEEMED
		if (paper.isTrading()) {
			paper.setOwner(newOwner);
		} else {
			throw new RuntimeException(
					"Paper " + issuer + paperNumber + " is not trading. Current state = " + paper.getState());
		}

		// Update the paper
		String key = State.makeComposite(CommercialPaper.class.getName(), new String[] { paperNumber });
		worldCollection.putState(key, CommercialPaper.serialize(paper));
		
		return paper;
	}

	/**
	 * Query for all the papers between the start and end keys
	 */
	public int sumPapersValue(Context ctx, String startPaper, String endPaper) {

		int totalValue = 0;
		
		// want to put this into the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getDefaultCollection();
		
		String startKey =  State.makeComposite(CommercialPaper.class.getName(), new String[] { startPaper }) ;
		String endKey =  State.makeComposite(CommercialPaper.class.getName(), new String[] { endPaper }) ;
		
		KeyQueryHandler keyQuery = KeyQueryHandler.RANGE;
		keyQuery.from( startKey ).to( endKey );
		
		CollectionIterable<State> states = worldCollection.getStates(keyQuery);
		for (State s : states) {
			CommercialPaper paper = CommercialPaper.deserialize(s.getValue());
			totalValue += paper.getFaceValue();
		}
		
		return totalValue;
	}
	
	/**
	 * Access the Commercial Paper History via the history state.
	 */
	public void paperHistory(Context ctx, String paperNumber) {
			
		// want to get this from the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getDefaultCollection();	
		
		// get the state
		State paperState = worldCollection.getState(paperNumber);
		
		CollectionIterable<StateHistory> states = paperState.getHistory();
		for (StateHistory s : states) {
			// APIs available on StateHistory
			Instant time = s.getTimestamp();
			State stateInTime = s.getState();
			String txid = s.getTxId();
			boolean isDeleted = s.isDeleted();
		}
		
		
	}
	
	/**
	 * Redeem commercial paper
	 *
	 * @param {Context} ctx the transaction context
	 * @param {String}  issuer commercial paper issuer
	 * @param {Integer} paperNumber paper number for this issuer
	 * @param {String}  redeemingOwner redeeming owner of paper
	 * @param {String}  redeemDateTime time paper was redeemed
	 */
	@Transaction(submit = true)
	public CommercialPaper redeem(CommercialPaperContext ctx, String issuer, String paperNumber, String redeemingOwner,
			String redeemDateTime) {

		// want to put this into the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getDefaultCollection();
			
		// get the state
		String key = State.makeComposite(CommercialPaper.class.getName(), new String[] { paperNumber });
		State paperState = worldCollection.getState(key);

		// and then to the Commercial Paper
		// this uses the same serialization approach as returning or passing objects
		// to the transaction functions.
		//
		CommercialPaper paper = CommercialPaper.deserialize(paperState.getValue());

		// Check paper is not REDEEMED
		if (paper.isRedeemed()) {
			throw new RuntimeException("Paper " + issuer + paperNumber + " already redeemed");
		}

		// Verify that the redeemer owns the commercial paper before redeeming it
		if (paper.getOwner().equals(redeemingOwner)) {
			paper.setOwner(paper.getIssuer());
			paper.setRedeemed();
		} else {
			throw new RuntimeException("Redeeming owner does not own paper" + issuer + paperNumber);
		}

		// Update the paper
		worldCollection.putState(paperNumber, CommercialPaper.serialize(paper));

		return paper;
	}
	
	/**
	 * Issue commercial paper
	 *
	 * @param {Context} ctx the transaction context
	 * @param {String}  issuer commercial paper issuer
	 * @param {Integer} paperNumber paper number for this issuer
	 * @param {String}  issueDateTime paper issue date
	 * @param {String}  maturityDateTime paper maturity date
	 * @param {Integer} faceValue face value of paper
	 */
	@Transaction(submit = true)
	public CommercialPaper issueEndorsed(CommercialPaperContext ctx, String issuer, String paperNumber, String issueDateTime,
			String maturityDateTime, int faceValue) {

		// create an instance of the paper
		CommercialPaper paper = CommercialPaper.createInstance(issuer, paperNumber, issueDateTime, maturityDateTime,
				faceValue, issuer, "");

		// Smart contract, rather than paper, moves paper into ISSUED state
		paper.setIssued();

		// Newly issued paper is owned by the issuer
		paper.setOwner(issuer);

		// want to put this into the public collection, aka 'world state'
		Collection worldCollection = Ledger.getLedger(ctx).getCollection(Collection.WORLD);
		String keyObj = State.makeComposite(CommercialPaper.class.getName(), new String[] { paperNumber });
		State paperState = worldCollection.putState(keyObj, CommercialPaper.serialize(paper));
		
		// How to use StateBased Endorsement
		// Create three principals
		Principal p1 = new Principal("Org1", Role.MEMBER);
		Principal p2 = new Principal("Org2", Role.MEMBER);
		Principal p3 = new Principal("Org3", Role.MEMBER);
		// create an Endorsment based on the required logic
		Endorsement sbe1 = Endorsement.build(
				Expression.and(
						Expression.or(p1,p2),
						p3
						)
				);
	    paperState.setEndorsement(sbe1);
		
		// OR
		StateBasedEndorsement sbe2 = StateBasedEndorsement.build(
			"AND( OR ('Org1.Member','Org2.Member') , 'Org3.Member'))");
		paperState.setEndorsement(sbe2);
		


		return paper;
	}
	

}