package edu.sdsu.tvidhate.pool_in.entity;

public class Request {

    private String requestorContact;
    private String requestorName;
    private String posterName;
    private String posterContact;
    private boolean approvalStatus = false;

    private User mJoinTripRequester;
    private User mTripApprover;

    @Override
    public String toString() {
        return "Request{" +
                "requestorContact='" + requestorContact + '\'' +
                ", requestorName='" + requestorName + '\'' +
                ", posterName='" + posterName + '\'' +
                ", posterContact='" + posterContact + '\'' +
                ", approvalStatus=" + approvalStatus +
                ", mJoinTripRequester=" + mJoinTripRequester +
                ", mTripApprover=" + mTripApprover +
                '}';
    }

    public User getmJoinTripRequester() {
        return mJoinTripRequester;
    }

    public void setmJoinTripRequester(User mJoinTripRequester) {
        this.mJoinTripRequester = mJoinTripRequester;
    }

    public User getmTripApprover() {
        return mTripApprover;
    }

    public void setmTripApprover(User mTripApprover) {
        this.mTripApprover = mTripApprover;
    }

    public boolean isApprovalStatus() {
        return approvalStatus;
    }

    public void setApprovalStatus(boolean approvalStatus) {
        this.approvalStatus = approvalStatus;
    }

    public String getRequestorContact() {
        return requestorContact;
    }

    public void setRequestorContact(String requestorContact) {
        this.requestorContact = requestorContact;
    }

    public String getRequestorName() {
        return requestorName;
    }

    public void setRequestorName(String requestorName) {
        this.requestorName = requestorName;
    }

    public String getPosterName() {
        return posterName;
    }

    public void setPosterName(String posterName) {
        this.posterName = posterName;
    }

    public String getPosterContact() {
        return posterContact;
    }

    public void setPosterContact(String posterContact) {
        this.posterContact = posterContact;
    }


}
