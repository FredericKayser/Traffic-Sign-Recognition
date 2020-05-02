package de.frederickayser.trafficsignrecognition.image;

import de.frederickayser.trafficsignrecognition.console.MessageBuilder;
import de.frederickayser.trafficsignrecognition.util.Util;
import lombok.Getter;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * by Frederic on 02.04.20(17:37)
 */
public class ImageFrame extends JFrame {

    private javax.swing.JButton confirmButton;
    private javax.swing.JLabel framesLabel;
    private javax.swing.JLabel imageLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel questionLabel;
    private javax.swing.JComboBox<String> selectionBox;
    private javax.swing.JProgressBar videoProgress;
    @Getter
    private AtomicBoolean submitted = new AtomicBoolean(true);

    private de.frederickayser.trafficsignrecognition.trafficsign.Type type = null;
    private final String settype;
    private BufferedImage bufferedImage;

    public ImageFrame(String settype) {
        this.settype = settype;
        initComponents();
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                setVisible(true);
            }
        });
    }

    public void updateImage(BufferedImage bufferedImage) {
        if(submitted.get()) {
            this.bufferedImage = bufferedImage;
            imageLabel.setText(" ");
            imageLabel.setIcon(new ImageIcon(bufferedImage));
            imageLabel.repaint();
            submitted.set(false);
        }
    }

    private void initComponents() {

        questionLabel = new javax.swing.JLabel();
        imageLabel = new javax.swing.JLabel();
        confirmButton = new javax.swing.JButton();
        selectionBox = new javax.swing.JComboBox<>();
        videoProgress = new javax.swing.JProgressBar();
        jLabel1 = new javax.swing.JLabel();
        framesLabel = new javax.swing.JLabel();

        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);

        questionLabel.setText("Welches Schild ist auf diesem Bild sichtbar?");

        imageLabel.setText("Berechne Bild...");

        confirmButton.setText("Bestätigen");
        confirmButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                confirmButtonActionPerformed(evt);
            }
        });

        selectionBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "30km/h", "50km/h", "70km/h", "80km/h", "80km/h aufgehoben", "100km/h", "120km/h", "Überholverbot", "Überholverbot aufgehoben", "Geschwindigkeitslimit und Überholverbot aufgehoben", "unbekannt" }));

        jLabel1.setText("Frame:");

        framesLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        framesLabel.setText("?/?");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(videoProgress, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(confirmButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                                        .addComponent(imageLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 469, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                        .addComponent(selectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, 273, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                .addGap(0, 57, Short.MAX_VALUE))
                                        .addGroup(layout.createSequentialGroup()
                                                .addComponent(questionLabel)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(jLabel1)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(framesLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addGap(9, 9, 9)))
                                .addContainerGap())
        );
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                                .addContainerGap()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(layout.createSequentialGroup()
                                                .addGap(0, 0, Short.MAX_VALUE)
                                                .addComponent(confirmButton))
                                        .addGroup(layout.createSequentialGroup()
                                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                                        .addComponent(questionLabel)
                                                        .addComponent(jLabel1)
                                                        .addComponent(framesLabel))
                                                .addGap(7, 7, 7)
                                                .addComponent(selectionBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                                .addComponent(imageLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                                                .addGap(12, 12, 12)
                                                .addComponent(videoProgress, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>

    private void confirmButtonActionPerformed(java.awt.event.ActionEvent evt) {
        if(!submitted.get() && bufferedImage != null) {
            switch (selectionBox.getSelectedIndex()) {
                case 0:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.THIRTY_KMH;
                    break;
                case 1:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.FIFTY_KMH;
                    break;
                case 2:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.SEVENTY_KMH;
                    break;
                case 3:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.EIGHTY_KMH;
                    break;
                case 4:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.EIGHTY_KMH_END;
                    break;
                case 5:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.HUNDRET_KMH;
                    break;
                case 6:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.HUNDRET_TWENTY_KMH;
                    break;
                case 7:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.OVERTAKE_FORBIDDEN;
                    break;
                case 8:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.OVERTAKE_FORBIDDEN_END;
                    break;
                case 9:
                    type = de.frederickayser.trafficsignrecognition.trafficsign.Type.SPEED_LIMIT_OVERTAKE_FORBIDDEN_END;
                    break;
            }

            if (settype.equals("training")) {
                DataPreparer.getInstance().saveTrainingImageTransformed(bufferedImage, type);
            } else {
                DataPreparer.getInstance().saveTestImageTransformed(bufferedImage, type);
            }
            submitted.set(true);
            imageLabel.setIcon(null);
            imageLabel.setText("Berechne nächstes Bild...");
        }
    }

    public de.frederickayser.trafficsignrecognition.trafficsign.Type getTafficSignType() {
        return type;
    }

    public void initProgress(int start, int maxLength) {
        videoProgress.setMinimum(0);
        videoProgress.setMaximum(maxLength);
        videoProgress.setValue(start);
        videoProgress.setStringPainted(true);
        videoProgress.setString("0%");
        framesLabel.setText(start + "/" + maxLength);
    }

    public void updateProgress(int current) {
        videoProgress.setValue(current);
        double percentage = Double.valueOf(current)/Double.valueOf(videoProgress.getMaximum());
        percentage = percentage * 100;
        percentage = Math.round(percentage);
        int percentageInt = (int) percentage;
        videoProgress.setString(percentageInt + "%");
        framesLabel.setText(current + "/" + videoProgress.getMaximum());
    }

}
