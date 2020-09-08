package com.neteru.afrikett.core.models.RemoteDB;

import android.content.Context;
import android.net.Uri;

import com.neteru.afrikett.R;

import java.io.Serializable;

import static com.neteru.afrikett.core.utilities.AppUtilities.getCurrentDate;
import static com.neteru.afrikett.core.utilities.Constants.MESSAGE_LOAD;

@SuppressWarnings("unused")
public class MessengerChat implements Serializable {

    private String messageId;
    private String senderId;
    private String recipientId;
    private String messageSendingDate;
    private String messageDownloadUrl;
    private String tempStorageLocation;
    private String videoThumbnailDownloadUrl;
    private String messageFinalPath;
    protected String messageText;
    protected String messageLegend;
    protected String messageFileName;
    protected String messageUriStr;
    protected String videoThumbnailUriStr;
    protected String contactName;
    protected String contactNumber;
    protected String contactEmail;
    protected String dateId;
    protected String mediaFileLength;
    protected String fileSize;
    private Integer messageType;
    private Integer messageState;

    public MessengerChat(){}

    protected MessengerChat(String id, String sender, String recipient, Integer type){

        this.messageId = id;
        this.senderId = sender;
        this.recipientId = recipient;
        this.messageType = type;
        this.messageState = MESSAGE_LOAD;
        this.messageSendingDate = getCurrentDate();

    }

    public String getMessageId() {
        return messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public String getMessageSendingDate() {
        return messageSendingDate;
    }

    public String getMessageText() {
        return messageText;
    }

    public String getMessageUriStr() {
        return messageUriStr;
    }

    public String getMessageFileName() {
        return messageFileName;
    }

    public String getMessageLegend() {
        return messageLegend;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public Integer getMessageType() {
        return messageType;
    }

    public String getDateId() {
        return dateId;
    }

    public String getMessageDownloadUrl() {
        return messageDownloadUrl;
    }

    public Integer getMessageState() {
        return messageState;
    }

    public String getMediaFileLength() {
        return mediaFileLength;
    }

    public String getFileSize() {
        return fileSize;
    }

    public String getTempStorageLocation() {
        return tempStorageLocation;
    }

    public String getVideoThumbnailUriStr() {
        return videoThumbnailUriStr;
    }

    public String getVideoThumbnailDownloadUrl() {
        return videoThumbnailDownloadUrl;
    }

    public String getMessageFinalPath() {
        return messageFinalPath;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public void setMessageSendingDate(String messageSendingDate) {
        this.messageSendingDate = messageSendingDate;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }

    public void setMessageUriStr(String messageUriStr) {
        this.messageUriStr = messageUriStr;
    }

    public void setMessageFileName(String messageFileName) {
        this.messageFileName = messageFileName;
    }

    public void setMessageLegend(String messageLegend) {
        this.messageLegend = messageLegend;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public void setMessageType(Integer messageType) {
        this.messageType = messageType;
    }

    public void setDateId(String dateId) {
        this.dateId = dateId;
    }

    public void setMessageDownloadUrl(String messageDownloadUrl) {
        this.messageDownloadUrl = messageDownloadUrl;
    }

    public void setMessageState(Integer messageState) {
        this.messageState = messageState;
    }

    public void setMediaFileLength(String mediaFileLength) {
        this.mediaFileLength = mediaFileLength;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public void setTempStorageLocation(String tempStorageLocation) {
        this.tempStorageLocation = tempStorageLocation;
    }

    public void setVideoThumbnailUriStr(String videoThumbnailUriStr) {
        this.videoThumbnailUriStr = videoThumbnailUriStr;
    }

    public void setVideoThumbnailDownloadUrl(String videoThumbnailDownloadUrl) {
        this.videoThumbnailDownloadUrl = videoThumbnailDownloadUrl;
    }

    public void setMessageFinalPath(String messageFinalPath) {
        this.messageFinalPath = messageFinalPath;
    }

    public Uri extractMessageUri(){
        return Uri.parse(this.messageUriStr);
    }

    public Uri extractVideoThumbnailUri(){
        return Uri.parse(this.videoThumbnailUriStr);
    }

    public void provideMessageUri(Uri uri){
        this.messageUriStr = uri.toString();
    }

    public void provideVideoThumbnailUri(Uri uri){
        this.videoThumbnailUriStr = uri.toString();
    }

    public static String getCurrentDateId(){
        return getCurrentDate("dd.MMM.yyyy");
    }

    public static String extractDateFromDateId(Context context, String dateId){

        String currentDate = getCurrentDateId(), result;
        String[] dateSegments = dateId.split("\\."),
                 currentDateSegments = currentDate.split("\\.");

        if (dateId.equals(currentDate)){
            result = context.getString(R.string.today);
        }else {

            if (dateSegments[1].equals(currentDateSegments[1]) && dateSegments[2].equals(currentDateSegments[2])) {

                Integer day = Integer.valueOf(dateId.split("\\.")[0]),
                        currentDay = Integer.valueOf(currentDate.split("\\.")[0]);

                if (currentDay - day == 1) {
                    result = context.getString(R.string.yesterday);
                } else if (currentDay - day == 2) {
                    result = context.getString(R.string.day_before_yesterday);
                } else {
                    result = dateId.replace(".", " ");
                }

            }else {
                result = dateId.replace(".", " ");
            }

        }

        return result;
    }
}
