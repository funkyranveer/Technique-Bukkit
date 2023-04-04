package scha.efer.technique.arena;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import scha.efer.technique.TechniquePlugin;
import scha.efer.technique.arena.impl.KoTHArena;
import scha.efer.technique.arena.impl.SharedArena;
import scha.efer.technique.arena.impl.StandaloneArena;
import scha.efer.technique.arena.impl.TheBridgeArena;
import scha.efer.technique.cuboid.Cuboid;
import scha.efer.technique.kit.Kit;
import scha.efer.technique.match.team.TeamType;
import scha.efer.technique.util.KothPoint;
import scha.efer.technique.util.Serializer;
import scha.efer.technique.util.external.CC;
import scha.efer.technique.util.external.LocationUtil;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class Arena {

    @Getter
    private static final List<Arena> arenas = new ArrayList<>();

    @Getter
    protected String name;
    @Setter
    protected Location spawn1;
    @Setter
    protected Location spawn2;
    @Setter
    protected Location bedLocationA;
    @Setter
    protected Location bedLocationB;
    @Setter
    protected Location point1;
    @Setter
    protected Location point2;
    @Getter
    protected boolean active;
    @Getter
    @Setter
    private KothPoint point;
    @Setter
    private ItemStack displayIcon;
    @Getter
    @Setter
    private List<String> kits = new ArrayList<>();
    @Getter @Setter
    private Claim bridgesRedClaim, bridgesBlueClaim;

    public Arena(String name) {
        this.name = name;
        this.displayIcon = new ItemStack(Material.PAPER);
        this.bridgesRedClaim = null;
        this.bridgesBlueClaim = null;
    }

    public static void init() {
        FileConfiguration configuration = TechniquePlugin.get().getArenasConfig().getConfiguration();

        if (configuration.contains("arenas")) {
            if (configuration.getConfigurationSection("arenas") == null) return;
            for (String arenaName : configuration.getConfigurationSection("arenas").getKeys(false)) {
                String path = "arenas." + arenaName;

                ArenaType arenaType = ArenaType.valueOf(configuration.getString(path + ".type"));

                Arena arena;

                if (arenaType == ArenaType.STANDALONE) {
                    arena = new StandaloneArena(arenaName);
                } else if (arenaType == ArenaType.SHARED) {
                    arena = new SharedArena(arenaName);
                } else if (arenaType == ArenaType.KOTH) {
                    arena = new KoTHArena(arenaName);
                } else if (arenaType == ArenaType.THEBRIDGE) {
                    arena = new TheBridgeArena(arenaName);
                }
                else {
                    continue;
                }

                //arena.setDisplayIcon(new ItemBuilder(Material.valueOf(configuration.getString(path + ".icon").build());


                if (configuration.contains(path + ".spawn1")) {
                    arena.setSpawn1(LocationUtil.deserialize(configuration.getString(path + ".spawn1")));
                }

                if (configuration.contains(path + ".spawn2")) {
                    arena.setSpawn2(LocationUtil.deserialize(configuration.getString(path + ".spawn2")));
                }

                if (configuration.contains(path + ".red_bed.coord")) {
                    arena.setBedLocationA(LocationUtil.deserialize(configuration.getString(path + ".red_bed.coord")));
                }

                if (configuration.contains(path + ".blue_bed.coord")) {
                    arena.setBedLocationB(LocationUtil.deserialize(configuration.getString(path + ".blue_bed.coord")));
                }

                if (arena instanceof TheBridgeArena && configuration.contains(path + ".bridges_claims.red")) {
                    arena.setBridgesRedClaim(Serializer.deserializeClaim(TeamType.TEAM_1, configuration.getString(path + ".bridges_claims.red")));
                }

                if (arena instanceof TheBridgeArena && configuration.contains(path + ".bridges_claims.blue")) {
                    arena.setBridgesBlueClaim(Serializer.deserializeClaim(TeamType.TEAM_2, configuration.getString(path + ".bridges_claims.blue")));
                }

                if (arena instanceof TheBridgeArena && configuration.contains(path + ".redCuboid") && configuration.contains(path + ".blueCuboid")) {
                    Location location1;
                    Location location2;
                    //Declare the arena as type TheBridge
                    TheBridgeArena standaloneArena = (TheBridgeArena) arena;

                    //If "redCuboid" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".redCuboid.location2"));
                    standaloneArena.setRedCuboid(new Cuboid(location1, location2));

                    //If "blueCuboid" location exist then load it
                    location1 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location1"));
                    location2 = LocationUtil.deserialize(configuration.getString(path + ".blueCuboid.location2"));
                    standaloneArena.setBlueCuboid(new Cuboid(location1, location2));
                }

                if (configuration.contains(path + ".point1") && configuration.contains(path + ".point2")) {
                    arena.setPoint1(LocationUtil.deserialize(configuration.getString(path + ".point1")));
                    arena.setPoint2(LocationUtil.deserialize(configuration.getString(path + ".point2")));
                    arena.setPoint(new KothPoint(arena.point1, arena.point2));
                }


                if (configuration.contains(path + ".kits")) {
                    for (String kitName : configuration.getStringList(path + ".kits")) {
                        arena.getKits().add(kitName);
                    }
                }

                if (arena instanceof StandaloneArena && configuration.contains(path + ".duplicates")) {
                    for (String duplicateId : configuration.getConfigurationSection(path + ".duplicates").getKeys(false)) {
                        Location spawn1 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn1"));
                        Location spawn2 = LocationUtil.deserialize(configuration.getString(path + ".duplicates." + duplicateId + ".spawn2"));

                        Arena duplicate = new Arena(arenaName);

                        duplicate.setSpawn1(spawn1);
                        duplicate.setSpawn2(spawn2);
                        duplicate.setKits(arena.getKits());

                        ((StandaloneArena) arena).getDuplicates().add(duplicate);

                        Arena.getArenas().add(duplicate);
                    }
                }

                Arena.getArenas().add(arena);
            }
        }

        TechniquePlugin.get().getLogger().info("Loaded " + Arena.getArenas().size() + " arenas");
    }

    public static ArenaType getTypeByName(String name) {
        for (ArenaType arena : ArenaType.values()) {
            if (arena.toString().equalsIgnoreCase(name)) {
                return arena;
            }
        }
        return null;
    }

    public static Arena getByName(String name) {
        for (Arena arena : arenas) {
            if (arena.getType() != ArenaType.DUPLICATE && arena.getName() != null &&
                    arena.getName().equalsIgnoreCase(name)) {
                return arena;
            }
        }

        return null;
    }

    public void setBedA(String name, Player player) {
        FileConfiguration config = TechniquePlugin.get().getArenasConfig().getConfiguration();
        config.set("arenas." + name + ".red_bed.x", (Object)player.getLocation().getBlockX());
        config.set("arenas." + name + ".red_bed.y", (Object)player.getLocation().getBlockY());
        config.set("arenas." + name + ".red_bed.z", (Object)player.getLocation().getBlockZ());

        try {
            config.save(TechniquePlugin.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage(CC.translate("&aYou have successfully set &cRed&a's bed!"));
    }

    public void setBedB(String name, Player player) {
        FileConfiguration config = TechniquePlugin.get().getArenasConfig().getConfiguration();
        config.set("arenas." + name + ".blue_bed.x", (Object)player.getLocation().getBlockX());
        config.set("arenas." + name + ".blue_bed.y", (Object)player.getLocation().getBlockY());
        config.set("arenas." + name + ".blue_bed.z", (Object)player.getLocation().getBlockZ());

        try {
            config.save(TechniquePlugin.get().getArenasConfig().getFile());
        } catch (IOException e) {
            e.printStackTrace();
        }
        player.sendMessage(CC.translate("&aYou have successfully set &9Blue&a's bed!"));
    }

    public static Arena getRandom(Kit kit) {
        List<Arena> _arenas = new ArrayList<>();

        for (Arena arena : arenas) {
            if (!arena.isSetup()) continue;

            if (!arena.getKits().contains(kit.getName())) continue;

            if (arena.getType() == ArenaType.KOTH) {
                _arenas.add(arena);
            }

            if (!arena.isActive() && (arena.getType() == ArenaType.STANDALONE || arena.getType() == ArenaType.DUPLICATE || arena.getType() == ArenaType.THEBRIDGE)) {
                _arenas.add(arena);
            } else if (!kit.getGameRules().isBuild() && arena.getType() == ArenaType.SHARED) {
                _arenas.add(arena);
            }
        }

        if (_arenas.isEmpty()) {
            return null;
        }

        return _arenas.get(ThreadLocalRandom.current().nextInt(_arenas.size()));
    }

    public ArenaType getType() {
        return ArenaType.DUPLICATE;
    }

    public boolean isSetup() {
        return spawn1 != null && spawn2 != null;
    }

    public int getMaxBuildHeight() {
        int highest = (int) (spawn1.getY() >= spawn2.getY() ? spawn1.getY() : spawn2.getY());
        return highest + 5;
    }

    public Location getBedLocationA() {
        if (bedLocationA == null) {
            return null;
        }

        return bedLocationA.clone();
    }

    public Location getBedLocationB() {
        if (bedLocationB == null) {
            return null;
        }

        return bedLocationB.clone();
    }

    public Location getSpawn1() {
        if (spawn1 == null) {
            return null;
        }

        return spawn1.clone();
    }

    public Location getSpawn2() {
        if (spawn2 == null) {
            return null;
        }

        return spawn2.clone();
    }

    public Location getPoint1() {
        if (point1 == null) {
            return null;
        }

        return point1.clone();
    }

    public Location getPoint2() {
        if (point2 == null) {
            return null;
        }

        return point2.clone();
    }
    public ItemStack getDisplayIcon() {
        return this.displayIcon.clone();
    }


    public void setActive(boolean active) {
        if (getType() != ArenaType.SHARED && getType() != ArenaType.KOTH) {
            this.active = active;
        }
    }
    public Claim getClaimAt(Location location) {
        if(this.bridgesRedClaim != null) {
            if(this.bridgesRedClaim.isInsideClaim(location)) return this.bridgesRedClaim;
        }

        if(this.bridgesBlueClaim != null) {
            if(this.bridgesBlueClaim.isInsideClaim(location)) return this.bridgesBlueClaim;
        }

        return null;
    }


    public void save() {

    }

    public void delete() {

    }

}
