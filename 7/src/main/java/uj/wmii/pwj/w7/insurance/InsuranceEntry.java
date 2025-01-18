package uj.wmii.pwj.w7.insurance;


public class InsuranceEntry {
    private int policyId;
    private String state;
    private String county;
    private double eqSiteLimit;
    private double huSiteLimit;
    private double flSiteLimit;
    private double frSiteLimit;
    private double tiv2011;
    private double tiv2012;
    private double eqSiteDeductible;
    private double huSiteDeductible;
    private double flSiteDeductible;
    private double frSiteDeductible;
    private double pointLatitude;
    private double pointLongitude;
    private String line;
    private String construction;
    private int pointGrading;

    public InsuranceEntry(String policyId, String state, String county, String eqSiteLimit, String huSiteLimit,
                           String flSiteLimit, String frSiteLimit, String tiv2011, String tiv2012,
                           String eqSiteDeductible, String huSiteDeductible, String flSiteDeductible,
                           String frSiteDeductible, String pointLatitude, String pointLongitude,
                           String line, String construction, String pointGrading) {


        this.policyId = Integer.parseInt(policyId); // Konwersja na int
        this.state = state;
        this.county = county;
        this.eqSiteLimit = Double.parseDouble(eqSiteLimit); // Konwersja na double
        this.huSiteLimit = Double.parseDouble(huSiteLimit);
        this.flSiteLimit = Double.parseDouble(flSiteLimit);
        this.frSiteLimit = Double.parseDouble(frSiteLimit);
        this.tiv2011 = Double.parseDouble(tiv2011);
        this.tiv2012 = Double.parseDouble(tiv2012);
        this.eqSiteDeductible = Double.parseDouble(eqSiteDeductible);
        this.huSiteDeductible = Double.parseDouble(huSiteDeductible);
        this.flSiteDeductible = Double.parseDouble(flSiteDeductible);
        this.frSiteDeductible = Double.parseDouble(frSiteDeductible);
        this.pointLatitude = Double.parseDouble(pointLatitude);
        this.pointLongitude = Double.parseDouble(pointLongitude);
        this.line = line;
        this.construction = construction;
        this.pointGrading = Integer.parseInt(pointGrading); // Konwersja na int
    }

    // Gettery i Settery
    public int getPolicyId() {
        return policyId;
    }

    public void setPolicyId(int policyId) {
        this.policyId = policyId;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCounty() {
        return county;
    }

    public void setCounty(String county) {
        this.county = county;
    }

    public double getEqSiteLimit() {
        return eqSiteLimit;
    }

    public void setEqSiteLimit(double eqSiteLimit) {
        this.eqSiteLimit = eqSiteLimit;
    }

    public double getHuSiteLimit() {
        return huSiteLimit;
    }

    public void setHuSiteLimit(double huSiteLimit) {
        this.huSiteLimit = huSiteLimit;
    }

    public double getFlSiteLimit() {
        return flSiteLimit;
    }

    public void setFlSiteLimit(double flSiteLimit) {
        this.flSiteLimit = flSiteLimit;
    }

    public double getFrSiteLimit() {
        return frSiteLimit;
    }

    public void setFrSiteLimit(double frSiteLimit) {
        this.frSiteLimit = frSiteLimit;
    }

    public double getTiv2011() {
        return tiv2011;
    }

    public void setTiv2011(double tiv2011) {
        this.tiv2011 = tiv2011;
    }

    public double getTiv2012() {
        return tiv2012;
    }

    public void setTiv2012(double tiv2012) {
        this.tiv2012 = tiv2012;
    }

    public double getEqSiteDeductible() {
        return eqSiteDeductible;
    }

    public void setEqSiteDeductible(double eqSiteDeductible) {
        this.eqSiteDeductible = eqSiteDeductible;
    }

    public double getHuSiteDeductible() {
        return huSiteDeductible;
    }

    public void setHuSiteDeductible(double huSiteDeductible) {
        this.huSiteDeductible = huSiteDeductible;
    }

    public double getFlSiteDeductible() {
        return flSiteDeductible;
    }

    public void setFlSiteDeductible(double flSiteDeductible) {
        this.flSiteDeductible = flSiteDeductible;
    }

    public double getFrSiteDeductible() {
        return frSiteDeductible;
    }

    public void setFrSiteDeductible(double frSiteDeductible) {
        this.frSiteDeductible = frSiteDeductible;
    }

    public double getPointLatitude() {
        return pointLatitude;
    }

    public void setPointLatitude(double pointLatitude) {
        this.pointLatitude = pointLatitude;
    }

    public double getPointLongitude() {
        return pointLongitude;
    }

    public void setPointLongitude(double pointLongitude) {
        this.pointLongitude = pointLongitude;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getConstruction() {
        return construction;
    }

    public void setConstruction(String construction) {
        this.construction = construction;
    }

    public int getPointGrading() {
        return pointGrading;
    }

    public void setPointGrading(int pointGrading) {
        this.pointGrading = pointGrading;
    }

    @Override
    public String toString() {
        return "InsuranceRecord{" +
                "policyId=" + policyId +
                ", state='" + state + '\'' +
                ", county='" + county + '\'' +
                ", eqSiteLimit=" + eqSiteLimit +
                ", huSiteLimit=" + huSiteLimit +
                ", flSiteLimit=" + flSiteLimit +
                ", frSiteLimit=" + frSiteLimit +
                ", tiv2011=" + tiv2011 +
                ", tiv2012=" + tiv2012 +
                ", eqSiteDeductible=" + eqSiteDeductible +
                ", huSiteDeductible=" + huSiteDeductible +
                ", flSiteDeductible=" + flSiteDeductible +
                ", frSiteDeductible=" + frSiteDeductible +
                ", pointLatitude=" + pointLatitude +
                ", pointLongitude=" + pointLongitude +
                ", line='" + line + '\'' +
                ", construction='" + construction + '\'' +
                ", pointGrading=" + pointGrading +
                '}';
    }
}
