/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Nadun
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class CCDASectionPreferenceListPanel {

    private double fractionOfExtentToScrollPerArrowClick;
    private double fractionOfExtentToScrollPerTrackClick;
    private JScrollBar scrollBar;
    private int scrollBarWidth; // The default width it normally has in any GUI.
    public JPanel container; // The container of it all.
    private int itemHeight = 0;  // The vertical extent of each contained panel.
    private int containerWidth = 500;  // Width of the container.
    private int itemCount = 0; // The amount of panels.
    private long contentSize = 0; // The sum total extent of all "contained panels".
    private long actualScrollPosition = 0; // The true scroll position, think contentSize.
    private final Dimension BUTTON_SIZE = new Dimension(200, 45);
    private final int BUTTON_GAP = 20;
    private Dimension lastKnownContainerSize = new Dimension(0, 0);
    private Color color; // color of the list panel

    private ArrayList<CCDASectionPreference> initialPreferenceList;
    // When saving the preferences this list will be updated.
    private ArrayList<CCDASectionPreference> currentPreferenceList;
    // This is relevent to the UI model. When any item change in the list map will be updated.
    private Map<Integer, CCDASectionPreference> preferenceMap = new HashMap<>();
    
    /**
     * @param fractionOfExtentToScrollPerArrowClick E.g. 0.1 for 10% of the visible area to become hidden/shown when you
     *                                              click a scrollbar arrow.
     * @param fractionOfExtentToScrollPerTrackClick E.g. 0.95 for 95% of the visible area to become hidden/shown when
     *                                              you click in the scrollbar track.
     * @param itemHeight                            Can later also be done via setter.
     * @param containerWidth                        Width of the container
     * @param listOfReferences                      List of references.
     * @param listColor                             Background color of the list panel
     */
    public CCDASectionPreferenceListPanel(double fractionOfExtentToScrollPerArrowClick,
                                          double fractionOfExtentToScrollPerTrackClick,
                                          int itemHeight,
                                          int containerWidth,
                                          ArrayList<CCDASectionPreference> listOfReferences,
                                          Color listColor) {
        
        this.fractionOfExtentToScrollPerArrowClick = Math.max(0, fractionOfExtentToScrollPerArrowClick);
        this.fractionOfExtentToScrollPerTrackClick = Math.max(0, fractionOfExtentToScrollPerTrackClick);
        this.itemCount = Math.max(0, listOfReferences.size());
        this.color = listColor;
        this.initialPreferenceList = copy(listOfReferences);
        this.currentPreferenceList = copy(listOfReferences);
        this.containerWidth = containerWidth;
        setItemHeight(itemHeight);
        
        // copy the given object list to the map
        for (CCDASectionPreference pref : copy(listOfReferences)) {
            this.preferenceMap.put(pref.getSortIndex(), pref);
        }
        
        scrollBarWidth = determineScrollBarDefaultWidth();
        scrollBar = new JScrollBar(JScrollBar.VERTICAL, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE);
        scrollBar.addAdjustmentListener(e -> update());

        // create a container for the UI
        container = new JPanel(null);
        container.setBackground(this.color);
        
        int actionButtonsPanelWidth = 2 * BUTTON_SIZE.height + 3*BUTTON_GAP;
        contentSize = (long) itemCount * (long) itemHeight + (long)actionButtonsPanelWidth;
        
        container.setPreferredSize(new Dimension(this.containerWidth, (int)contentSize));
        container.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(final ComponentEvent e) {
                update();
            }
        });
        
        container.setSize(this.containerWidth, (int)contentSize);

    }

    private int determineScrollBarDefaultWidth() {

        JScrollPane dummyForDefaultSize = new JScrollPane(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                                                                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        dummyForDefaultSize.setPreferredSize(new Dimension(1000, 1000));
        dummyForDefaultSize.setSize(dummyForDefaultSize.getPreferredSize());
        dummyForDefaultSize.doLayout();
        return dummyForDefaultSize.getVerticalScrollBar().getSize().width;
    }

    public void setItemHeight(int height) {
        this.itemHeight = Math.max(1, height);
    }

    public void setContainerWidth(int containerWidth) {
        this.containerWidth = containerWidth;
    }

    public void setItemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public int getContainerWidth() {
        return containerWidth;
    }
    
    public int getContainerHeight() {
        return (int)contentSize;
    }

    public int getItemCount() {
        return itemCount;
    }

    public Color getColor() {
        return color;
    }
    
    public Dimension getDimension(){
        return new Dimension(getContainerWidth(), getContainerHeight());
    }
    
    /**
     * 
     * @return the current list of preferences after saving or before saving
     */
    public ArrayList<CCDASectionPreference> getItemList(){
        return this.currentPreferenceList;
    }
    
    /**
     * This layouts the component. This is done automatically when the scrollbar is moved or the container is resized,
     * but any other action would require YOU to call this.
     */
    public void update() {

        container.removeAll();
        lastKnownContainerSize = container.getSize();

        int containerSize;
        
        scrollBar.setLocation(lastKnownContainerSize.width - scrollBarWidth, 0);
        scrollBar.setSize(scrollBarWidth, lastKnownContainerSize.height);
        containerSize = lastKnownContainerSize.height;
        
        long invisibleStuff = contentSize - containerSize;
        actualScrollPosition = Math.max(0, Math.min(invisibleStuff,(long) (getScrollBarPosRatio() * (invisibleStuff))));

        int extent;
        if (contentSize > 0) {
            double visibleRatio = containerSize / (double) contentSize;
            extent = (int) Math.max(0, Math.min(Integer.MAX_VALUE, Integer.MAX_VALUE * visibleRatio));
        } else {
            extent = Integer.MAX_VALUE;
        }
        
        int unitIncrement = (int) Math.max(1, Math.min(extent, extent * fractionOfExtentToScrollPerArrowClick));
        int blockIncrement = (int) Math.max(1, Math.min(extent, extent * fractionOfExtentToScrollPerTrackClick));
        scrollBar.getModel().setExtent(extent);
        scrollBar.setUnitIncrement(unitIncrement);
        scrollBar.setBlockIncrement(blockIncrement);
        scrollBar.setVisible(extent < Integer.MAX_VALUE);

        Dimension panelSizes = getPanelSize();
        
        int n = 0;
        while (n < itemCount) { // Loop ongoing = need more panels to fill the view.

            // Calc index of current panel.
            int panelIndex = n;

            JPanel panel = supplyPreferencePanel(panelIndex);
            container.add(panel);
            panel.revalidate();

            // Set position and size.
            int panelPos = (int) ((n * itemHeight) - actualScrollPosition);
            Point location = new Point(0, panelPos);
            
            panel.setLocation(location);
            panel.setSize(panelSizes);

            n++;
        }
        
        // create save & reset buttons
        JButton saveBtn = new JButton("Save Preferences");
        JButton resetBtn = new JButton("Reset Preferences");
        saveBtn.setFocusPainted(false);
        resetBtn.setFocusPainted(false);
        container.add(saveBtn);
        container.add(resetBtn);
        
        // Set position and size.
        int panelPos = (int) ((n * itemHeight) - actualScrollPosition);
        Point location = new Point((panelSizes.width - BUTTON_SIZE.width) / 2, panelPos + BUTTON_GAP);

        saveBtn.setLocation(location);
        saveBtn.setSize(BUTTON_SIZE);
        
        resetBtn.setLocation(location.x, location.y + BUTTON_SIZE.height + BUTTON_GAP);
        resetBtn.setSize(BUTTON_SIZE);

        // Layout.
        container.add(scrollBar);
        
        container.revalidate();
        container.repaint(); // required
        
        // button action listners
        saveBtn.addActionListener(e -> {
            SavePreferences();
        });
        
        resetBtn.addActionListener(e -> {
            ResetPreferences();
            // update the UI
            update();
        });
    }

    private Dimension getPanelSize() {
        return new Dimension(lastKnownContainerSize.width - (scrollBar.isVisible() ? scrollBarWidth : 0), itemHeight);
    }
    
    private double getScrollBarPosRatio() {

        int scrollRangeSize = Integer.MAX_VALUE - scrollBar.getVisibleAmount(); // Which should really be named getExtent(). Or rather the other way round.
        return scrollBar.getValue() / (double) scrollRangeSize;
    }
    
    private void SavePreferences(){
        
        currentPreferenceList.clear();
        for (Map.Entry<Integer, CCDASectionPreference> entrySet : preferenceMap.entrySet()) {
            Integer key = entrySet.getKey();
            CCDASectionPreference value = entrySet.getValue();
            CCDASectionPreference newValue = new CCDASectionPreference(
                value.getName(), value.isSelected(), value.getSortIndex());
            this.currentPreferenceList.add(key, newValue);
        }
    }
    
    private void ResetPreferences(){
        for (CCDASectionPreference pref : copy(initialPreferenceList)) {
            preferenceMap.put(pref.getSortIndex(), pref);
        }
        SavePreferences();
    }
    
    /**
     * @param list - providing list of objects
     * @return deep copy of given preferences list
     */
    private ArrayList<CCDASectionPreference> copy(ArrayList<CCDASectionPreference> list){
        ArrayList<CCDASectionPreference> copy = new ArrayList<>();
        for (CCDASectionPreference pref : list) {
            CCDASectionPreference newPref = new CCDASectionPreference(
                    pref.getName(), pref.isSelected(), pref.getSortIndex());
            copy.add(newPref);
        }
        return copy;
    }
    
    /**
     * 
     * @param panelIndex - the given sort index 
     * @return a JPanel relevant to the given sort index
     */
    private JPanel supplyPreferencePanel(int panelIndex) {

        CCDASectionPreference preference = this.preferenceMap.get(panelIndex);
        // check box 
        JCheckBox checkBox = new JCheckBox(preference.getName(),preference.isSelected());
        checkBox.setFocusPainted(false);
        checkBox.setHorizontalAlignment(SwingConstants.CENTER);
        checkBox.setVerticalAlignment(SwingConstants.CENTER);
        checkBox.addItemListener(e -> {
            preference.setSelected(checkBox.isSelected());
        });

        // up-down buttons
        JButton buttonUp = new JButton("UP");
        JButton buttonDown = new JButton("DOWN");
        buttonUp.setFocusPainted(false);
        buttonDown.setFocusPainted(false);
        
        // disable the up button of the first item & the down button of last item
        if(panelIndex == 0)
            buttonUp.setEnabled(false);
        if(panelIndex == this.itemCount - 1)
            buttonDown.setEnabled(false);
        
        // Add up button action listner
        buttonUp.addActionListener(e -> {
            
            CCDASectionPreference up = this.preferenceMap.get(panelIndex - 1);
            CCDASectionPreference current = this.preferenceMap.get(panelIndex);
            String upName = up.getName();
            String currentName = current.getName();
            boolean upSelected = up.isSelected();
            boolean currentSelected = current.isSelected();
            this.preferenceMap.get(panelIndex - 1).setName(currentName);
            this.preferenceMap.get(panelIndex).setName(upName);
            this.preferenceMap.get(panelIndex - 1).setSelected(currentSelected);
            this.preferenceMap.get(panelIndex).setSelected(upSelected);
            update();
        });
        
        // Add down button action listner
        buttonDown.addActionListener(e -> {
            CCDASectionPreference down = this.preferenceMap.get(panelIndex + 1);
            CCDASectionPreference current = this.preferenceMap.get(panelIndex);
            String downName = down.getName();
            String currentName = current.getName();
            boolean downSelected = down.isSelected();
            boolean currentSelected = current.isSelected();
            this.preferenceMap.get(panelIndex + 1).setName(currentName);
            this.preferenceMap.get(panelIndex).setName(downName);
            this.preferenceMap.get(panelIndex + 1).setSelected(currentSelected);
            this.preferenceMap.get(panelIndex).setSelected(downSelected);
            update();
        });

        // Jpanel for the item content
        JPanel panel = new JPanel(new BorderLayout(0, 0));
        panel.setBorder(BorderFactory.createLineBorder(this.color, 8, false));
        panel.add(checkBox, BorderLayout.WEST);
        
        // up-down button panel
        JPanel moveButtonsPanel = new JPanel(new BorderLayout(0, 0));
        moveButtonsPanel.add(buttonUp, BorderLayout.WEST);
        moveButtonsPanel.add(buttonDown, BorderLayout.EAST);
        panel.add(moveButtonsPanel, BorderLayout.EAST);

        return panel;
    }
}
