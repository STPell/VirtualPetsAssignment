package virtualpets;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.Color;
import java.util.ArrayList;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JButton;

/**
 * A panel to handle pet being fed.
 * @author Samuel Pell
 */
@SuppressWarnings("serial")
public class FeedPanel extends JPanel {
    /**
     * Panel which displays food the player has.
     */
    private InventoryPanel itemListPanel;
    /**
     * Label that show the food's name.
     */
    private JLabel lblName;
    /**
     * Label that displays the food's description.
     */
    private JLabel lblDescription;
    /**
     * Label to show the user the food's portion size.
     */
    private JLabel lblPortionScore;

    /**
     * Create the panel.
     */
    public FeedPanel() {
        setLayout(null);

        itemListPanel = new InventoryPanel();
        itemListPanel.addEventListener(new ListSelectionListener() {
            /**
             * When selection changes display new items stats.
             */
            public void valueChanged(ListSelectionEvent e) {
                Item selected = itemListPanel.getSelectedItem();
                showItemStats(selected);
            }
        });
        itemListPanel.setBounds(22, 39, 189, 333);
        add(itemListPanel);

        JLabel lblInventory = new JLabel("Inventory");
        lblInventory.setBounds(22, 14, 64, 14);
        add(lblInventory);

        JSeparator separator = new JSeparator();
        separator.setForeground(Color.BLACK);
        separator.setOrientation(SwingConstants.VERTICAL);
        separator.setBounds(221, 39, 14, 333);
        add(separator);

        lblName = new JLabel("");
        lblName.setBounds(235, 52, 189, 14);
        add(lblName);

        lblDescription = new JLabel("");
        lblDescription.setBounds(235, 76, 331, 14);
        add(lblDescription);

        JLabel lblDurability = new JLabel("Portion Size: ");
        lblDurability.setBounds(235, 101, 85, 14);
        add(lblDurability);

        JButton btnPlayWithToy = new JButton("Feed");
        btnPlayWithToy.setBounds(656, 349, 104, 23);
        add(btnPlayWithToy);

        lblPortionScore = new JLabel("");
        lblPortionScore.setBounds(300, 101, 189, 14);
        add(lblPortionScore);
    }

    /**
     * Shows the player the currently selected item from their inventory.
     * @param selected Item to display information for.
     */
    private void showItemStats(Item selected) {
        if (selected != null) {
            Food selectedToy = (Food) selected;
            lblName.setText(selected.getName());

            //Format description string
            String description = selected.getDescription() + ".";
            Character firstChar = description.charAt(0);
            firstChar = Character.toUpperCase(firstChar);
            description = firstChar + description.substring(1);
            lblDescription.setText(description);

            Integer portionSize = (Integer) selectedToy.getPortionSize();
            lblPortionScore.setText(portionSize.toString());
        } else {  //if item selected is null - this happens when itemListPanel is refreshed.
            lblName.setText("");
            lblDescription.setText("");
            lblPortionScore.setText("");
        }
    }

    /**
     * List the toys in the players inventory.
     * @param foodList List of toys the player owns
     */
    public void listPlayerFood(ArrayList<Food> foodList){
        Item[] itemList = foodList.toArray(new Item[0]);
        itemListPanel.displayInventory(itemList);
    }

    /**
     * Testing functionality of this panel.
     * @param args Arguments pased in from command line.
     */
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            //ignore all exceptions 
        }

        ArrayList<Food> foodList = new ArrayList<Food>();
        for (int i = 0; i < 30; i++) {
            foodList.add(new Food("Tuna", "Fishy", 5, 20));
        }
        JFrame myFrame = new JFrame();

        myFrame.setBounds(0, 0, 785, 423);
        myFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        myFrame.getContentPane().setLayout(null);

        FeedPanel myPanel = new FeedPanel();
        myPanel.listPlayerFood(foodList);

        myFrame.getContentPane().add(myPanel);
        myPanel.setVisible(true);

        myPanel.setSize(770, 383);
        myFrame.setVisible(true);
    }
}
