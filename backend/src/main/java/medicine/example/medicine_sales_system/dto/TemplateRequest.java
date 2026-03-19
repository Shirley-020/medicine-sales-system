package medicine.example.medicine_sales_system.dto;

public class TemplateRequest {
    private String name;
    private String rules; // 简化为字符串
    private String actions;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRules() { return rules; }
    public void setRules(String rules) { this.rules = rules; }
    public String getActions() { return actions; }
    public void setActions(String actions) { this.actions = actions; }
}
