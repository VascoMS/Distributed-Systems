package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.tecnico.distledger.server.domain.ServerState;
import pt.ulisboa.tecnico.distledger.contract.user.*;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

import static io.grpc.Status.INVALID_ARGUMENT;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase {

    private ServerState server = new ServerState();

    @Override
    public void balance(BalanceRequest request, StreamObserver<BalanceResponse> responseObserver) {
        int balance = server.getBalance(request.getUserId());
        if (balance == -1) {
            responseObserver.onError(INVALID_ARGUMENT.withDescription("Account does not exist").asRuntimeException());
        } else {
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
        } else{
            CreateAccountResponse response = CreateAccountResponse.getDefaultInstance();
            // Send a single response through the stream.
            responseObserver.onNext(response);
            // Notify the client that the operation has been completed.
            responseObserver.onCompleted();
        }
    }

    @Override
    public void deleteAccount(DeleteAccountRequest request, StreamObserver<DeleteAccountResponse> responseObserver) {
        DeleteAccountResponse response = DeleteAccountResponse.getDefaultInstance();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }

    @Override
    public void transferTo(TransferToRequest request, StreamObserver<TransferToResponse> responseObserver) {
        TransferToResponse response = TransferToResponse.getDefaultInstance();
        // Send a single response through the stream.
        responseObserver.onNext(response);
        // Notify the client that the operation has been completed.
        responseObserver.onCompleted();
    }
}
