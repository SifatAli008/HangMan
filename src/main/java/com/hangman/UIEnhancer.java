package com.hangman;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.Glow;
import javafx.scene.paint.Color;
import javafx.util.Duration;

/**
 * UI Enhancement utility class for modern visual effects
 */
public class UIEnhancer {
    
    /**
     * Applies modern button effects with hover animations
     */
    public static void enhanceButton(Button button) {
        // Create shadow effects
        DropShadow defaultShadow = new DropShadow();
        defaultShadow.setRadius(5.0);
        defaultShadow.setOffsetX(0.0);
        defaultShadow.setOffsetY(2.0);
        defaultShadow.setColor(Color.rgb(0, 0, 0, 0.2));
        
        DropShadow hoverShadow = new DropShadow();
        hoverShadow.setRadius(8.0);
        hoverShadow.setOffsetX(0.0);
        hoverShadow.setOffsetY(4.0);
        hoverShadow.setColor(Color.rgb(0, 0, 0, 0.3));
        
        // Set initial effect
        button.setEffect(defaultShadow);
        
        // Add hover effects
        button.setOnMouseEntered(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.05);
            scale.setToY(1.05);
            scale.play();
            button.setEffect(hoverShadow);
        });
        
        button.setOnMouseExited(e -> {
            ScaleTransition scale = new ScaleTransition(Duration.millis(150), button);
            scale.setToX(1.0);
            scale.setToY(1.0);
            scale.play();
            button.setEffect(defaultShadow);
        });
    }
    
    /**
     * Adds a fade-in effect to any node
     */
    public static void addFadeInEffect(Node node) {
        node.setOpacity(0.0);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(800), node);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }
    
    /**
     * Adds a success animation to a node
     */
    public static void addSuccessAnimation(Node node) {
        ScaleTransition scaleUp = new ScaleTransition(Duration.millis(200), node);
        scaleUp.setToX(1.2);
        scaleUp.setToY(1.2);
        
        ScaleTransition scaleDown = new ScaleTransition(Duration.millis(200), node);
        scaleDown.setToX(1.0);
        scaleDown.setToY(1.0);
        
        SequentialTransition sequence = new SequentialTransition(scaleUp, scaleDown);
        sequence.play();
    }
    
    /**
     * Adds an error animation to a node
     */
    public static void addErrorAnimation(Node node) {
        TranslateTransition shake = new TranslateTransition(Duration.millis(50), node);
        shake.setByX(-5);
        shake.setCycleCount(6);
        shake.setAutoReverse(true);
        shake.play();
    }
    
    /**
     * Adds a pulse effect to a node
     */
    public static void addPulseEffect(Node node) {
        ScaleTransition pulse = new ScaleTransition(Duration.millis(500), node);
        pulse.setToX(1.1);
        pulse.setToY(1.1);
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.setAutoReverse(true);
        pulse.play();
    }
    
    /**
     * Adds a glow effect to a label
     */
    public static void addGlowEffect(Label label, Color glowColor) {
        Glow glow = new Glow();
        glow.setLevel(0.8);
        label.setEffect(glow);
        
        Timeline glowPulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(glow.levelProperty(), 0.3)),
            new KeyFrame(Duration.millis(1000), new KeyValue(glow.levelProperty(), 0.8)),
            new KeyFrame(Duration.millis(2000), new KeyValue(glow.levelProperty(), 0.3))
        );
        glowPulse.setCycleCount(3);
        glowPulse.setOnFinished(e -> label.setEffect(null));
        glowPulse.play();
    }
    
    /**
     * Adds a bounce effect to a node
     */
    public static void addBounceEffect(Node node) {
        Timeline bounce = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(node.translateYProperty(), 0)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateYProperty(), -10)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateYProperty(), 0)),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateYProperty(), -5)),
            new KeyFrame(Duration.millis(400), new KeyValue(node.translateYProperty(), 0))
        );
        bounce.play();
    }
    
    /**
     * Adds a slide-in effect from left to right
     */
    public static void addSlideInEffect(Node node, double fromX, double toX) {
        node.setTranslateX(fromX);
        TranslateTransition slideIn = new TranslateTransition(Duration.millis(600), node);
        slideIn.setFromX(fromX);
        slideIn.setToX(toX);
        slideIn.play();
    }
    
    /**
     * Stops all animations on a node
     */
    public static void stopAnimations(Node node) {
        // Stop any running animations
        node.getTransforms().clear();
        node.setTranslateX(0);
        node.setTranslateY(0);
        node.setScaleX(1.0);
        node.setScaleY(1.0);
        node.setRotate(0);
    }
}
