package pt.tecnico.distledger.server.service;

import io.grpc.stub.StreamObserver;
import pt.ulisboa.tecnico.distledger.contract.user.UserDistLedger;
import pt.ulisboa.tecnico.distledger.contract.user.UserServiceGrpc;

public class UserServiceImpl extends UserServiceGrpc.UserServiceImplBase{

    @Override
    public void balance(UserDistLedger.BalanceRequest request, StreamObserver<UserDistLedger.BalanceResponse> responseObserver) {
    }

    @Override
    public void createAccount(UserDistLedger.CreateAccountRequest request, StreamObserver<UserDistLedger.CreateAccountResponse> responseObserver) {
    }

    @Override
    public void deleteAccount(UserDistLedger.DeleteAccountRequest request, StreamObserver<UserDistLedger.DeleteAccountResponse> responseObserver) {
    }

    @Override
    public void transferTo(UserDistLedger.TransferToRequest request, StreamObserver<UserDistLedger.TransferToResponse> responseObserver) {
    }
}
