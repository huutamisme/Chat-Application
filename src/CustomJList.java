import javax.swing.*;
import java.awt.*;
public class CustomJList {

    static class BorderedAndCenteredTextListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            label.setHorizontalAlignment(SwingConstants.LEFT);

            Icon avatar = getScaledIcon("unknown.jpg", 30, 30);
            label.setIcon(avatar);

            String displayText = value.toString();
            if(index == 0){
                displayText = value.toString() + " (You)";
            }
            //label.setText("<html><left>" + displayText + "<br><font color='green'>Online</font></left></html>");
            label.setText(displayText);
            return label;
        }

        private Icon getScaledIcon(String path, int width, int height) {
            ImageIcon originalIcon = new ImageIcon(path);
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
    }

    static class GroupChatListCellRender extends DefaultListCellRenderer{
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

            label.setBorder(BorderFactory.createLineBorder(Color.GRAY));

            label.setHorizontalAlignment(SwingConstants.LEFT);

            Icon avatar = getScaledIcon("favicon.png", 30, 30);
            label.setIcon(avatar);

            label.setText(value.toString());
            return label;
        }

        private Icon getScaledIcon(String path, int width, int height) {
            ImageIcon originalIcon = new ImageIcon(path);
            Image originalImage = originalIcon.getImage();
            Image scaledImage = originalImage.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        }
    }

}
