package com.mcstaralliance.godofwealth.task;

import com.mcstaralliance.godofwealth.GodOfWealth;
import com.mcstaralliance.godofwealth.util.ConfigUtil;
import com.mcstaralliance.godofwealth.util.RewardUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class CheckRunnable extends BukkitRunnable {
    private static final GodOfWealth plugin = GodOfWealth.getInstance();
    private boolean hasClearedData = false;

    private void broadcastSelectedMessage(Player player) {
        String lang = plugin.getConfig().getString("lang.broadcast-selected-player").replaceAll("%player%", player.getName());
        Bukkit.broadcastMessage(ConfigUtil.color(lang));
    }

    private void selectLuckyPlayer() {
        List<Player> players = new ArrayList<>(Bukkit.getOnlinePlayers());
        if (players.isEmpty()) {
            return;
        }
        Random random = new Random();
        int randomNumber = random.nextInt(players.size());
        Player player = players.get(randomNumber);
        ConfigUtil.saveData(player);
        broadcastSelectedMessage(player);
    }

    @Override
    public void run() {
        // in charge of selecting lucky player & rewarding.
        LocalTime now = LocalTime.now();
        boolean hasCompletedToday = plugin.getConfig().getBoolean("selection.hasCompletedToday");
        boolean tomorrowComes = now.getHour() == 0;
        boolean isDuringRewardTime = now.getHour() > plugin.getConfig().getInt("reward.after")
                && now.getHour() < plugin.getConfig().getInt("selection.time");
        boolean isSelectionTime = now.getHour() == plugin.getConfig().getInt(("selection.time"));
        Player player = Bukkit.getPlayer(UUID.fromString(plugin.getConfig().getString("lucky-player")));

        if (tomorrowComes) {
            if (hasClearedData) {
                return;
            }
            ConfigUtil.clearSelectionStatus();
            hasClearedData = true;
            return;
        }
        if (hasCompletedToday) {
            // 阻止新的财神爷产生
            return;
        }
        if (isDuringRewardTime) {
            if (player == null) {
                return;
            } else if (player.isOnline()) {
                RewardUtil.rewardAllPlayers();
                ConfigUtil.clearData();
            }
            if (isSelectionTime) {
                hasClearedData = false;
                selectLuckyPlayer();
            }
        }
    }
}
