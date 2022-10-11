package no.vestlandetmc.limbo.obj;

import java.util.UUID;

import org.bukkit.Bukkit;

public class CachePlayer {

	private final UUID uuid;
	private final long timestamp;
	private final long expire;
	private final String reason;
	private final UUID staffUUID;


	public CachePlayer(UUID uuid, UUID staffUUID, long timestamp, long expire, String reason) {
		this.uuid = uuid;
		this.staffUUID = staffUUID;
		this.timestamp = timestamp;
		this.expire = expire;
		this.reason = reason;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public String getName() {
		return Bukkit.getOfflinePlayer(this.uuid).getName();
	}

	public UUID getUniqueId() {
		return this.uuid;
	}

	public String getReason() {
		return this.reason;
	}

	public long getExpire() {
		return this.expire;
	}

	public UUID getStaffUUID() {
		return this.staffUUID;
	}

}
