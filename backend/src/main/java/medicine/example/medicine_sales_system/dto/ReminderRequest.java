package medicine.example.medicine_sales_system.dto;

public class ReminderRequest {
    private String channel;
    private String message;

    public String getChannel() { return channel; }
    public void setChannel(String channel) { this.channel = channel; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
