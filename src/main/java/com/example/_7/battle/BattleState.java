package com.example._7.battle;

import com.example._7.character.CharacterStats;
import com.example._7.status.Buff;
import com.example._7.status.Debuff;

import java.util.ArrayList;
import java.util.List;

public class BattleState {
    private int currentHp;
    private int currentStamina;
    private int currentMana;
    private int currentShield;

    // 儲存最大值，供 UI 或其他邏輯查詢
    private int maxHp;
    private int maxStamina;
    private int maxMana;

    // Constructor: 先將所有數值設為 0，透過 resetFrom 將角色最大數值填入
    private final List<Buff> buffs;
    private final List<Debuff> debuffs;

    public BattleState() {
        this.currentHp = 0;
        this.currentStamina = 0;
        this.currentMana = 0;
        this.currentShield = 0;

        this.maxHp = 0;
        this.maxStamina = 0;
        this.maxMana = 0;

        this.buffs = new ArrayList<>();
        this.debuffs = new ArrayList<>();
    }

    /**
     * 根據角色屬性初始化戰鬥狀態（設定最大值與當前值）
     */
    public void resetFrom(CharacterStats characterStats) {
        if (characterStats == null) return;

        this.maxHp = characterStats.getMaxHp();
        this.maxStamina = characterStats.getMaxStamina();
        this.maxMana = characterStats.getMaxMana();

        this.currentHp = this.maxHp;
        this.currentStamina = this.maxStamina;
        this.currentMana = this.maxMana;
        this.currentShield = 0;

        // 清空/初始化狀態效果列表
        this.buffs.clear();
        this.debuffs.clear();
    }

    public boolean isDead() {
        return currentHp <= 0;
    }

    // current getters / setters
    public int getCurrentHp() {
        return currentHp;
    }

    public void setCurrentHp(int hp) {
        this.currentHp = Math.max(hp, Integer.MIN_VALUE); // 可依需求加上上限/下限檢查
    }

    public int getCurrentStamina() {
        return currentStamina;
    }

    public void setCurrentStamina(int stamina) {
        this.currentStamina = stamina;
    }

    public int getCurrentMana() {
        return currentMana;
    }

    public void setCurrentMana(int mana) {
        this.currentMana = mana;
    }

    public int getCurrentShield() {
        return currentShield;
    }

    // max getters（新增，供 UI 使用）
    public int getMaxHp() {
        return maxHp;
    }

    public int getMaxStamina() {
        return maxStamina;
    }

    public int getMaxMana() {
        return maxMana;
    }

    public List<Buff> getBuffs() {
        return List.copyOf(buffs);
    }

    public List<Debuff> getDebuffs() {
        return List.copyOf(debuffs);
    }

    public void takeDamage(int damage) {
        if (damage <= 0) return;

        int remainingDamage = damage;

        if (currentShield > 0) {
            int blocked = Math.min(currentShield, remainingDamage);
            currentShield -= blocked;
            remainingDamage -= blocked;
        }

        if (remainingDamage > 0) {
            currentHp = Math.max(0, currentHp - remainingDamage);
        }
    }

    public void heal(int amount, int maxHp) {
        if (amount <= 0) return;
        currentHp = Math.min(maxHp, currentHp + amount);
    }

    public void addShield(int amount) {
        if (amount <= 0) return;
        currentShield += amount;
    }

    public void recoverStamina(int amount, int maxStamina) {
        if (amount <= 0) return;
        currentStamina = Math.min(maxStamina, currentStamina + amount);
    }

    public void recoverMana(int amount, int maxMana) {
        if (amount <= 0) return;
        currentMana = Math.min(maxMana, currentMana + amount);
    }

    public boolean hasEnoughStamina(int cost) {
        return currentStamina >= cost;
    }

    public boolean hasEnoughMana(int cost) {
        return currentMana >= cost;
    }

    public void useStamina(int amount) {
        if (amount <= 0) return;
        currentStamina = Math.max(0, currentStamina - amount);
    }

    public void useMana(int amount) {
        if (amount <= 0) return;
        currentMana = Math.max(0, currentMana - amount);
    }
}