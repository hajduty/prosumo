package me.hajder.testplugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SumoEvent implements Listener {
    private final SumoPlugin plugin;
    public static boolean sumoInProgress = false;
    private static List<Player> playersInCurrentRound = new ArrayList<>(); // players playing now
    private static List<Player> allPlayers = new ArrayList<>(); // all players not lost
    private static List<Player> nextRound = new ArrayList<>(); // players gone to next round
    private static int sumoRound = 0;
    private static Location sumoWorld;
    private static Location player1World;
    private static Location player2World;

    public SumoEvent(SumoPlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (sumoInProgress) {
            Player player = event.getPlayer();
            if (playersInCurrentRound.contains(player)) {
                if (player.getLocation().getY() < (Double) SumoSettings.getInstance().get("lowestY")) {
                    player.sendMessage("Du har åkt ur tävlingen, loool!");
                    player.teleport(sumoWorld);
                    playersInCurrentRound.remove(player);
                    allPlayers.remove(player);
                    if (playersInCurrentRound.size() == 1) {
                        nextRound.add(playersInCurrentRound.get(0));
                        playersInCurrentRound.clear();
                        startRound();
                    }
                }
            }
        }
    }

    @EventHandler
    public void onHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player) {
            Player p = (Player) event.getEntity();
            if (playersInCurrentRound.contains(p))
            {
                event.setCancelled(false);
                event.setDamage(0.0D);
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = (Player) event.getPlayer();
        playersInCurrentRound.remove(p);
        allPlayers.remove(p);
        nextRound.remove(p);
    }
    public static void setupSumo()
    {
        SumoPlugin.getInstance().getLogger().info("Setting up sumo!");

        sumoWorld = getLocations("spawn");
        player1World = getLocations("player1");
        player2World = getLocations("player2");

        playersInCurrentRound.clear();
        nextRound.clear();
        allPlayers.clear();

        new BukkitRunnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.teleport(sumoWorld);
                    allPlayers.add(player);
                }
                SumoPlugin.getInstance().getLogger().info("Players sent to Sumo World!");

                startRound();
            }
        }.runTaskLater(SumoPlugin.getInstance(), 200L); // 20 ticks per second, 10 seconds = 200 ticks

        sumoInProgress = true;
    }

    public static void startRound() {
        SumoPlugin.getInstance().getLogger().info("Starting rounds soon!");

        if (allPlayers.size() == 1 && !nextRound.isEmpty()) {
            Player winner = allPlayers.get(0);
            Bukkit.broadcastMessage(winner.getName() + " Har vunnit!");
            sumoInProgress = false;
            return;
        } else if (allPlayers.size() == 1) {
            nextRound.add(allPlayers.get(0));
        }

        if (!sumoInProgress)
            return;

        if (playersInCurrentRound.isEmpty()) {
            SumoPlugin.getInstance().getServer().broadcastMessage("Ny runda börjar!");
            sumoRound++;
            playersInCurrentRound.addAll(nextRound);
            nextRound.clear();
        } else if (playersInCurrentRound.size() == 1) {
            nextRound.add(playersInCurrentRound.get(0));
        }

        //if (!(playersInCurrentRound.contains(nextRound))) {
        Player player1 = allPlayers.get(0);
        Player player2 = allPlayers.get(1);
        playersInCurrentRound.add(player1);
        playersInCurrentRound.add(player2);

        glassBlocks(player2World, true);
        glassBlocks(player1World, true);
        player1.teleport(player1World);
        player2.teleport(player2World);

        new BukkitRunnable() {
            @Override
            public void run() {
                glassBlocks(player1World, false);
                glassBlocks(player2World, false);
            }
        }.runTaskLater(SumoPlugin.getInstance(), 20 * 3); // 20 ticks per seconds

    }

    public static boolean sumoProgress() {
        SumoPlugin.getInstance().getLogger().info("Sumo progress = " + sumoInProgress);
        return sumoInProgress;
    }

    public static Location getLocations(String location) {
        double x = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".X");
        double y = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Y");
        double z = (Double) SumoSettings.getInstance().get("sumo-cords." + location + ".Z");

        World world = Bukkit.getWorld(SumoSettings.getInstance().get("sumo-cords." + location + ".World").toString());
        Location getLocation = new Location(world, x, y, z);
        return getLocation;
    }

    private static void glassBlocks(Location location, boolean setBlocks) {
        World world = location.getWorld();
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();

        // Determine the material based on the action
        Material glassMaterial = setBlocks ? Material.GLASS : Material.AIR;

        // Modify glass blocks around the location
        world.getBlockAt(x, y, z).setType(glassMaterial);
        world.getBlockAt(x + 1, y, z).setType(glassMaterial);
        world.getBlockAt(x - 1, y, z).setType(glassMaterial);
        world.getBlockAt(x, y, z + 1).setType(glassMaterial);
        world.getBlockAt(x, y, z - 1).setType(glassMaterial);
    }

}
