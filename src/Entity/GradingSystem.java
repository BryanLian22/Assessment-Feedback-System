
package Entity;

/**
 * Letter Grade Grading System (A, B, C, D, E, F)
 * Grade is determined by percentage: (marks / totalMarks) * 100
 * Admin can set minimum percentage thresholds for each grade
 */
public class GradingSystem {
    private double aMin;  // Grade A minimum percentage (default 80)
    private double bMin;  // Grade B minimum percentage (default 65)
    private double cMin;  // Grade C minimum percentage (default 50)
    private double dMin;  // Grade D minimum percentage (default 40)
    private double eMin;  // Grade E minimum percentage (default 30)
    // Below E is F

    public GradingSystem(double aMin, double bMin, double cMin, double dMin, double eMin) {
        this.aMin = aMin;
        this.bMin = bMin;
        this.cMin = cMin;
        this.dMin = dMin;
        this.eMin = eMin;
    }

    /**
     * Calculate percentage from marks
     * Formula: (marks / totalMarks) * 100
     * @param marks obtained marks
     * @param totalMarks maximum marks
     * @return Percentage value (0.0 - 100.0)
     */
    public static double calculatePercentage(double marks, double totalMarks) {
        if (totalMarks <= 0) return 0.0;
        double percentage = (marks / totalMarks) * 100.0;
        return Math.min(100.0, Math.max(0.0, percentage)); // Clamp between 0 and 100
    }

    /**
     * Get the letter grade based on percentage
     * @param percentage Percentage value (0.0 - 100.0)
     * @return Letter grade (A, B, C, D, E, or F)
     */
    public String getGrade(double percentage) {
        if (percentage >= aMin) return "A";
        else if (percentage >= bMin) return "B";
        else if (percentage >= cMin) return "C";
        else if (percentage >= dMin) return "D";
        else if (percentage >= eMin) return "E";
        return "F";
    }

    /**
     * Get the full grade description based on percentage
     * @param percentage Percentage value (0.0 - 100.0)
     * @return Full grade description
     */
    public String getGradeName(double percentage) {
        if (percentage >= aMin) return "Excellent";
        else if (percentage >= bMin) return "Good";
        else if (percentage >= cMin) return "Satisfactory";
        else if (percentage >= dMin) return "Pass";
        else if (percentage >= eMin) return "Marginal";
        return "Fail";
    }

    /**
     * Get formatted grade string with percentage
     * @param percentage Percentage value (0.0 - 100.0)
     * @return Formatted string like "A (85.00%)"
     */
    public String getGradeWithPercentage(double percentage) {
        return getGrade(percentage) + " (" + String.format("%.2f%%", percentage) + ")";
    }

    /**
     * Get grade from marks directly
     * @param marks obtained marks
     * @param totalMarks maximum marks
     * @return Letter grade (A, B, C, D, E, or F)
     */
    public String getGradeFromMarks(double marks, double totalMarks) {
        return getGrade(calculatePercentage(marks, totalMarks));
    }

    /**
     * Get grade description from marks directly
     * @param marks obtained marks
     * @param totalMarks maximum marks
     * @return Full grade description
     */
    public String getGradeNameFromMarks(double marks, double totalMarks) {
        return getGradeName(calculatePercentage(marks, totalMarks));
    }

    // Setters (Percentage-based thresholds)
    public void setAMin(double aMin) {
        this.aMin = aMin;
    }

    public void setBMin(double bMin) {
        this.bMin = bMin;
    }

    public void setCMin(double cMin) {
        this.cMin = cMin;
    }

    public void setDMin(double dMin) {
        this.dMin = dMin;
    }

    public void setEMin(double eMin) {
        this.eMin = eMin;
    }

    // Getters (Percentage-based thresholds)
    public double getAMin() {
        return aMin;
    }

    public double getBMin() {
        return bMin;
    }

    public double getCMin() {
        return cMin;
    }

    public double getDMin() {
        return dMin;
    }

    public double getEMin() {
        return eMin;
    }
}
