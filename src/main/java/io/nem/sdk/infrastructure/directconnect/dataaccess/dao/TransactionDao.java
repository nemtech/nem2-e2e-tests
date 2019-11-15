/**
 * ** Copyright (c) 2016-present,
 * ** Jaguar0625, gimre, BloodyRookie, Tech Bureau, Corp. All rights reserved.
 * **
 * ** This file is part of Catapult.
 * **
 * ** Catapult is free software: you can redistribute it and/or modify
 * ** it under the terms of the GNU Lesser General Public License as published by
 * ** the Free Software Foundation, either version 3 of the License, or
 * ** (at your option) any later version.
 * **
 * ** Catapult is distributed in the hope that it will be useful,
 * ** but WITHOUT ANY WARRANTY; without even the implied warranty of
 * ** MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * ** GNU Lesser General Public License for more details.
 * **
 * ** You should have received a copy of the GNU Lesser General Public License
 * ** along with Catapult. If not, see <http://www.gnu.org/licenses/>.
 **/

package io.nem.sdk.infrastructure.directconnect.dataaccess.dao;

import io.nem.core.utils.RetryCommand;
import io.nem.sdk.infrastructure.common.CatapultContext;
import io.nem.sdk.infrastructure.common.TransactionRepository;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.common.TransactionState;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.PartialTransactionsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.TransactionStatusesCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.TransactionsCollection;
import io.nem.sdk.infrastructure.directconnect.dataaccess.database.mongoDb.UnconfirmedTransactionsCollection;
import io.nem.sdk.infrastructure.directconnect.network.TransactionConnection;
import io.nem.sdk.model.transaction.*;
import io.reactivex.Observable;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Transaction dao repository.
 */
public class TransactionDao implements TransactionRepository {
	/* Catapult context. */
	private final CatapultContext catapultContext;

	/**
	 * Constructor.
	 *
	 * @param context Catapult context.
	 */
	public TransactionDao(final CatapultContext context) {
		this.catapultContext = context;
	}

	/**
	 * Gets a transaction for a given hash.
	 *
	 * @param transactionHash Transaction hash.
	 * @return Observable of Transaction.
	 */
	@Override
	public Observable<Transaction> getTransaction(final String transactionHash) {
		return Observable.fromCallable(
				() -> {
					Optional<Transaction> transaction =
							new TransactionsCollection(catapultContext.getDataAccessContext()).findByHash(transactionHash);
					if (transaction.isPresent()) {
						return transaction.get();
					}
					throw new IllegalArgumentException("Transaction hash " + transactionHash + " not found.");
				});
	}

	/**
	 * Gets a transaction status for a transaction hash.
	 *
	 * @param transactionHash Transaction hash.
	 * @return Observable of TransactionStatus.
	 */
	@Override
	public Observable<TransactionStatus> getTransactionStatus(final String transactionHash) {
		return Observable.fromCallable(
				() -> {
					final List<TransactionState> transactionStates =
							Arrays.asList(
									new UnconfirmedTransactionsCollection(catapultContext.getDataAccessContext()),
									new PartialTransactionsCollection(catapultContext.getDataAccessContext()),
									new TransactionsCollection(catapultContext.getDataAccessContext()),
									new TransactionStatusesCollection(catapultContext.getDataAccessContext()));
					final int maxRetries = 3; // Add retry since the tx could be between collections.
					final int waitTimeInMilliseconds = 1000;
					return new RetryCommand<TransactionStatus>(
							maxRetries, waitTimeInMilliseconds, Optional.empty())
							.run(
									(final RetryCommand<TransactionStatus> retryCommand) -> {
										for (final TransactionState transactionState : transactionStates) {
											final Optional<TransactionStatus> transactionStatus =
													transactionState.getStatus(transactionHash);
											if (transactionStatus.isPresent()) {
												return transactionStatus.get();
											}
										}
										throw new IllegalArgumentException(
												"Transaction hash " + transactionHash + " not found.");
									});
				});
	}

	/**
	 * Sends a signed transaction.
	 *
	 * @param signedTransaction Signed transaction.
	 * @return Observable of TransactionAnnounceResponse.
	 */
	@Override
	public Observable<TransactionAnnounceResponse> announce(
			final SignedTransaction signedTransaction) {
		return Observable.fromCallable(
				() -> {
					new TransactionConnection(catapultContext.getApiNodeContext().getAuthenticatedSocket())
							.announce(signedTransaction);
					return new TransactionAnnounceResponse("Success");
				});
	}

	/**
	 * Send a signed transaction with missing signatures.
	 *
	 * @param signedTransaction SignedTransaction
	 * @return Observable of TransactionAnnounceResponse.
	 */
	@Override
	public Observable<TransactionAnnounceResponse> announceAggregateBonded(
			SignedTransaction signedTransaction) {
		return Observable.fromCallable(
				() -> {
					new TransactionConnection(catapultContext.getApiNodeContext().getAuthenticatedSocket())
							.announceAggregateBonded(signedTransaction);
					return new TransactionAnnounceResponse("Success");
				});
	}

	/**
	 * Send a cosignature signed transaction of an already announced transaction.
	 *
	 * @param cosignatureSignedTransaction Cosignature signed transaction
	 * @return Observable of TransactionAnnounceResponse
	 */
	@Override
	public Observable<TransactionAnnounceResponse> announceAggregateBondedCosignature(
			CosignatureSignedTransaction cosignatureSignedTransaction) {
		return Observable.fromCallable(
				() -> {
					new TransactionConnection(catapultContext.getApiNodeContext().getAuthenticatedSocket())
							.announceAggregateBondedCosignature(cosignatureSignedTransaction);
					return new TransactionAnnounceResponse("Success");
				});
	}
}
