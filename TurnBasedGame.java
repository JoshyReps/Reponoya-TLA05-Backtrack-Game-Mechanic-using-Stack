package com.mycompany.turnbasedgame;

import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class TurnBasedGame {

    public static Random random = new Random();
    
    public static void main(String[] args) {

        Scanner s = new Scanner(System.in);
        
        //                                HP    Name   Max  Min                  Passive Ability
        Character player = new Character(100, "Player", 10, 1, Passive.assignPassive("none"));
        Character bot = new Character(100, "Bot", 10, 1, Passive.assignPassive("Heal"));
        
        int gameTime = 1;
        
        while(true) {
            
            if(gameTime % 2 != 0) {
                
                System.out.println("""
                               
                               Player HP : %s HP
                               Bot HP : %s HP
                               
                               Actions :
                               `>> type `attack`
                               `>> type `stun`
                               `>> type `skip`
                               """.formatted(player.playerHP, bot.playerHP));
                
                System.out.print("Enter Action : ");
                String actionStringInput = s.nextLine().trim().toLowerCase();
                
                System.out.println("\n----------- Player at Play! ------------");
                inputAction(player, bot, actionStringInput);
                System.out.println("------------------------------------------");
                
                if(bot.playerHP <= 0) {
                    System.out.println("You Won"); break;
                }
                
            }
            else {
                
                String randomBotChoice = switch (random.nextInt(3) + 1) {case 1 -> "attack"; case 2 -> "stun"; case 3 -> "skip"; default -> "ran";}; 
                
                System.out.println("\n----- Bot at Play! (Random Choice) -----");
                if(random.nextInt(4) + 1 == 4) bot.passive();
                inputAction(bot, player, randomBotChoice);
                System.out.println("------------------------------------------");
                
                if(player.playerHP <= 0) {
                    System.out.println("You Lost"); break;
                }
            }
            gameTime++;
        }
    }

    
    static void inputAction (Character character, Character enemy, String stringInput) {
        
        if(character.stunned != 0) {
            System.out.printf("You are Stunned By %d turns left %n", character.stunned);
            character.stunned--; 
            return;
        }
        
        switch(stringInput) {
            case "attack" -> {
                character.attack(enemy);
            }
            case "stun" -> {
                character.stun(enemy);
            }
            case "skip" -> {
                System.out.println("Skipped Turn");
            }
            default -> {
                System.out.println("That is not a valid Action!!");
            }
        }
    }
}

class Character {
        
    public static Random random = new Random();

    Stack<Integer> playerHPStack = new Stack<>();
    public String playerName;
    public int playerHP;
    public int playerDMG;
    public int playerMaxDMG;
    public int playerMinDMG;
    public int stunned;
    
    Passive passive;

    public Character(int playerHP, String playerName, int playerMaxDMG, int playerMinDMG, Passive passive) {
        this.playerName = playerName;
        this.playerHP = playerHP;
        this.playerMaxDMG = playerMaxDMG;
        this.playerMinDMG = playerMinDMG;
        this.passive = passive;
        playerHPStack.push(playerHP);
    }

    public void attack(Character enemy) {

        playerDMG = random.nextInt(playerMaxDMG) + playerMinDMG;
        System.out.print("""
                        %s has dealt %d Damage
                        %s has now %d HP
                         """.formatted( playerName, playerDMG,
                                        enemy.playerName, 
                                        enemy.damageAttack(playerDMG)));
        enemy.playerHPStack.push(enemy.playerHP);
    }

    public void stun(Character enemy) {
        
        int stunAmount = random.nextInt(3) + 1;
        System.out.printf("%s have Stun %s by %d Turn!%n".formatted(playerName, 
                                                          enemy.playerName, 
                                                          enemy.stunned = stunAmount));
    }
    
    public void passive() {
        if(passive != null) passive.passiveAbility(this);
    }
    
    public int damageAttack(int damageDealth) {
        if(playerHP - damageDealth <= 0) playerHP = 0;
        else playerHP -= damageDealth;
        return playerHP;
    }
}

interface Passive {
    
    public void passiveAbility (Character character);
    
    public static Passive assignPassive(String passive) {
        return switch(passive) {
            case "Heal" -> new HealPassive();
            default -> null;
        };
    }
    
    class HealPassive implements Passive {
        @Override
        public void passiveAbility(Character character) {
            if(character.playerHPStack.size() <= 1) return;
            character.playerHPStack.pop();
            character.playerHP = character.playerHPStack.peek();
            System.out.printf("%s's *Passive Healing Ability* has healed itself back to %s%n", character.playerName, character.playerHP);
        }
    }
}