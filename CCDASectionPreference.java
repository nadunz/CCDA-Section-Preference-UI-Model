/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nadun
 */
public class CCDASectionPreference {
    
    private String name;
    private boolean selected;
    private int sortIndex;

    public CCDASectionPreference() {
    }

    public CCDASectionPreference(String name, boolean selected, int sortIndex) {
        this.name = name;
        this.selected = selected;
        this.sortIndex = sortIndex;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public int getSortIndex() {
        return sortIndex;
    }

    public void setSortIndex(int sortIndex) {
        this.sortIndex = sortIndex;
    }
    
    @Override
    public String toString(){
        return "[" + this.getName() 
                + "," + this.getSortIndex() 
                + "," + this.isSelected() + "]";
    }
}
