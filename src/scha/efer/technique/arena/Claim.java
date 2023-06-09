package scha.efer.technique.arena;

import org.bukkit.Location;

import lombok.Getter;
import lombok.Setter;
import scha.efer.technique.match.team.TeamType;

@Getter @Setter
public class Claim {

	private TeamType ownerTeam;
	@Getter
	@Setter
	private Location corner1;
	@Getter
	@Setter
	private Location corner2;

	public Claim(TeamType ownerTeam, Location corner1, Location corner2) {
		this.ownerTeam = ownerTeam;
		this.corner1 = corner1;
		this.corner2 = corner2;
	}

	public boolean isInsideClaim(Location location) {
		return (location.getBlockX() >= Math.min(corner1.getBlockX(), corner2.getBlockX()))
				&& (location.getBlockZ() >= Math.min(corner1.getBlockZ(), corner2.getBlockZ()))
				&& (location.getBlockX() <= Math.max(corner1.getBlockX(), corner2.getBlockX()))
				&& (location.getBlockZ() <= Math.max(corner1.getBlockZ(), corner2.getBlockZ()));
	}
}
