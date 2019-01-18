package com.atomikos.recovery.xa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.atomikos.datasource.xa.XID;

public class InMemoryPreviousXidRepository implements PreviousXidRepository {

	private Map<Long,List<XID>> cache = new HashMap<>();
	
	@Override
	public synchronized List<XID> findXidsExpiredAt(long startOfRecoveryScan) {
		List<XID> xids = new ArrayList<>();
		for (Long expiration : cache.keySet()) {
			if(expiration<startOfRecoveryScan) {
				xids.addAll(cache.get(expiration));
			}
		}
		return xids;
	}

	@Override
	public synchronized void remember(List<XID> xidsToStoreForNextScan, long expiration) {
		if (!xidsToStoreForNextScan.isEmpty()) {
			cache.put(expiration, xidsToStoreForNextScan);
		}

	}

	
	@Override
	public synchronized void forgetXidsExpiredAt(long startOfRecoveryScan) {
		Iterator<Long> it = cache.keySet().iterator();
		while (it.hasNext()) {
			Long expiration = it.next();
			if(expiration<=startOfRecoveryScan) {
				it.remove();
			}
		}
	}

}
