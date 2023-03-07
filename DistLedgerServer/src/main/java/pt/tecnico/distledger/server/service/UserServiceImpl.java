package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.user.*;

import static io.grpc.Status.INVALID_ARGUMENT;
import static io.grpc.Status.UNAVAILABLE;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private ServerState server = new ServerState();

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        OperationResult result = server.balanceVerification(request.getUserId());
        if(result == OperationResult.NO_ACCOUNT_FOUND){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Account does not exist").asRuntimeException());
        }
        else if(result == OperationResult.SERVER_OFF){
            responseObserver.onError(UNAVAILABLE.withDescription("UNAVAILABLE").asRuntimeException());
        }
        else {
            int balance = server.getBalance(request.getUserId());
            BalanceResponse response = BalanceResponse.newBuilder().setValue(balance).build();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void createAccount(CreateAccountRequest request, StreamObserver<CreateAccountResponse> responseObserver) {
        OperationResult result = server.createAccount(request.getUserId());
        if(result == OperationResult.ACCOUNT_ALREADY_EXISTS){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Account already exists").asRuntimeException());
        }
        else if(result == OperationResult.SERVER_OFF){
            responseObserver.onError(UNAVAILABLE.withDescription("UNAVAILABLE").asRuntimeException());
        }
        else{
            CreateAccountResponse response = CreateAccountResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
        OperationResult result = server.deleteAccount(request.getUserId());
        if(result == OperationResult.NO_ACCOUNT_FOUND){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Account does not exist").asRuntimeException());
        }
        else if(result == OperationResult.AMOUNT_NOT_0){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Balance is not zero").asRuntimeException());
        }
        else if(result == OperationResult.DELETE_BROKER){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Broker can not be deleted").asRuntimeException());
        }
        else if(result == OperationResult.SERVER_OFF){
            responseObserver.onError(UNAVAILABLE.withDescription("UNAVAILABLE").asRuntimeException());
        }
        else {
            DeleteAccountResponse response = DeleteAccountResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        OperationResult result = server.transferTo(request.getAccountFrom(), request.getAccountTo(), request.getAmount());
        if(result == OperationResult.SENDER_NOT_FOUND){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Sender does not exist").asRuntimeException());
        }
        else if(result == OperationResult.RECEIVER_NOT_FOUND){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Receiver does not exist").asRuntimeException());
        }
        else if(result == OperationResult.NOT_ENOUGH_MONEY){
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Insufficient balance").asRuntimeException());
        }
        else if(result == OperationResult.SERVER_OFF){
            responseObserver.onError(UNAVAILABLE.withDescription("UNAVAILABLE").asRuntimeException());
        }
        else{
            TransferToResponse response = TransferToResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }
}
