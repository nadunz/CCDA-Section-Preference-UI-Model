# CCDA-Section-Preference-UI-Model

CCDA - Consolidated Clinical Document Architecture

This is a java UI model can be intergrated to any GUI for a given objects list. The UI-model displays a list of objects(items) which can be moved up-down & have multiple selections.


## Usage

```java
Point LOCATION = new Point(150, 50);
Color COLOR = Color.BLUE;
int ITEM_HEIGHT = 50;
int CONTAINER_WIDTH = 300;

/**
 *  sort index should be 0,1,2,....,n
 */

// make a list of preferences 
ArrayList<CCDASectionPreference> references = new ArrayList<>();
CCDASectionPreference ref0 = new CCDASectionPreference("preference 0", true, 0);
CCDASectionPreference ref1 = new CCDASectionPreference("preference 1", false, 1);
CCDASectionPreference ref2 = new CCDASectionPreference("preference 2", true, 2);
CCDASectionPreference ref3 = new CCDASectionPreference("preference 3", false, 3);
CCDASectionPreference ref4 = new CCDASectionPreference("preference 4", true, 4);
references.add(ref0);
references.add(ref1);
references.add(ref2);
references.add(ref3);
references.add(ref4);

// jframe as the parent of the panel
JFrame frame = new JFrame("CCDASectionPreference Demo");
JPanel mainPanel = new JPanel(null);
mainPanel.setPreferredSize(new Dimension(600,600));

// create a panel with the list of preferences
CCDASectionPreferenceListPanel preferenceList = new CCDASectionPreferenceListPanel(
                                                    0.1,
                                                    0.95,
                                                    ITEM_HEIGHT,
                                                    CONTAINER_WIDTH,
                                                    references,
                                                    COLOR);
JPanel preferencePanel = preferenceList.container; // get the container of the UI model
preferencePanel.setLocation(LOCATION); // set location
mainPanel.add(preferencePanel); // add to the parent component 

frame.add(mainPanel);
frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
frame.pack();
frame.setLocationRelativeTo(null);
frame.setVisible(true);

// any time, if you need the latest saved preferences list, use following method
ArrayList<CCDASectionPreference> itemList = preferenceList.getItemList();
System.out.println(itemList.toString());
```

## Compile & Run the Demo Class

Use Netbeans or cmd

  - `$ javac CCDASectionPreferenceDemo.java`
  - `$ java CCDASectionPreferenceDemo`

## UI-Model View

![img-uimodel](https://user-images.githubusercontent.com/34955038/62441961-d7bb2900-b773-11e9-8f5b-a340581e2572.PNG)
 
