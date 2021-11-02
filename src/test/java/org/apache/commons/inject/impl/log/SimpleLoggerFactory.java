package org.apache.commons.inject.impl.log;

import java.util.ArrayList;
import java.util.List;

public class SimpleLoggerFactory {
	public enum Level {
		TRACE("TRACE"),
		DEBUG("DEBUG"),
		INFO("INFO "),
		WARN("WARN "),
		ERROR("ERROR"),
		FATAL("FATAL");
		final String value;
		private Level(String pValue) {
			value = pValue;
		}
		public String getValue() {
			return value;
		}
	}
	public interface SimpleLogger {
		void log(Level pLevel, String pMsg);
		void log(String pMsg);
	}

	private final List<String> messages = new ArrayList<String>();

	public int getNumEvents() {
		synchronized(messages) {
			return messages.size();
		}
	}

	public SimpleLogger getLogger(final String pId) {
		return new SimpleLogger() {
			private final String id = pId;

			@Override
			public void log(Level pLevel, String pMsg) {
				final StringBuilder sb = new StringBuilder();
				sb.append(pLevel.getValue());
				sb.append(' ');
				sb.append(id);
				sb.append(' ');
				sb.append(pMsg);
				synchronized (messages) {
					messages.add(sb.toString());
				}
			}

			@Override
			public void log(String pMsg) {
				log(Level.DEBUG, pMsg);
			}
			
		};
	}

	public Object getMessage(int i) {
		synchronized(messages) {
			return messages.get(i);
		}
	}
}
