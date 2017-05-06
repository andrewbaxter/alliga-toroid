package com.zarbosoft.bonestruct.editor.banner;

import com.zarbosoft.bonestruct.display.Text;
import com.zarbosoft.bonestruct.editor.Context;
import com.zarbosoft.bonestruct.editor.IdleTask;
import com.zarbosoft.bonestruct.editor.Selection;
import com.zarbosoft.bonestruct.editor.visual.Visual;
import com.zarbosoft.bonestruct.editor.visual.attachments.VisualAttachmentAdapter;
import com.zarbosoft.bonestruct.syntax.style.Style;
import com.zarbosoft.bonestruct.wall.Attachment;
import com.zarbosoft.bonestruct.wall.Bedding;
import com.zarbosoft.bonestruct.wall.Brick;
import com.zarbosoft.rendaw.common.ChainComparator;
import org.pcollections.HashTreePSet;

import java.util.PriorityQueue;
import java.util.Timer;
import java.util.TimerTask;

public class Banner {
	public Text text;
	private final PriorityQueue<BannerMessage> queue =
			new PriorityQueue<>(11, new ChainComparator<BannerMessage>().greaterFirst(m -> m.priority).build());
	private BannerMessage current;
	private final Timer timer = new Timer();
	private Brick brick;
	private int transverse;
	private int scroll;
	private Bedding bedding;
	private IdlePlace idle;
	private final Attachment attachment = new Attachment() {
		@Override
		public void setTransverse(final Context context, final int transverse) {
			Banner.this.transverse = transverse;
			idlePlace(context);
		}

		@Override
		public void destroy(final Context context) {
			brick = null;
		}
	};

	private void idlePlace(final Context context) {
		if (text == null)
			return;
		if (idle == null) {
			idle = new IdlePlace(context);
			context.addIdle(idle);
		}
	}

	public void setScroll(final Context context, final int scroll) {
		this.scroll = scroll;
		idlePlace(context);
	}

	public void tagsChanged(final Context context) {
		updateStyle(context);
	}

	private class IdlePlace extends IdleTask {
		private final Context context;

		private IdlePlace(final Context context) {
			this.context = context;
		}

		@Override
		protected boolean runImplementation() {
			if (text != null) {
				text.setTransverse(context,
						Math.max(scroll, Banner.this.transverse - (int) text.font().getDescent()),
						false
				);
			}
			idle = null;
			return false;
		}

		@Override
		protected void destroyed() {
			idle = null;
		}
	}

	public Banner(final Context context) {
		context.addSelectionListener(new Context.SelectionListener() {
			@Override
			public void selectionChanged(final Context context, final Selection selection) {
				selection.addBrickListener(context, new VisualAttachmentAdapter.BoundsListener() {
					@Override
					public void firstChanged(final Context context, final Brick first) {
						if (brick != null) {
							if (bedding != null)
								brick.removeBedding(context, bedding);
							brick.removeAttachment(context, attachment);
						}
						brick = first;
						if (bedding != null) {
							brick.addBedding(context, bedding);
						}
						brick.addAttachment(context, attachment);
					}

					@Override
					public void lastChanged(final Context context, final Brick last) {

					}
				});
			}
		});
	}

	public void addMessage(final Context context, final BannerMessage message) {
		if (queue.isEmpty()) {
			text = context.display.text();
			final Style.Baked style = getStyle(context);
			text.setFont(context, style.getFont(context));
			text.setColor(context, style.color);
			context.midground.add(text);
			text.setTransverse(context, Math.max(scroll, transverse - text.font().getDescent()), false);
			bedding = new Bedding(text.transverseSpan(context), 0);
			brick.addBedding(context, bedding);
		}
		queue.add(message);
		update(context);
	}

	private Style.Baked getStyle(final Context context) {
		return context.getStyle(HashTreePSet.from(context.globalTags).plus(new Visual.PartTag("banner")));
	}

	private void updateStyle(final Context context) {
		if (text == null)
			return;
		final Style.Baked style = getStyle(context);
		text.setFont(context, style.getFont(context));
		text.setColor(context, style.color);
	}

	private void update(final Context context) {
		if (queue.isEmpty()) {
			if (text != null) {
				context.midground.remove(text);
				text = null;
				brick.removeBedding(context, bedding);
				bedding = null;
			}
		} else if (queue.peek() != current) {
			current = queue.peek();
			text.setText(context, current.text);
			timer.purge();
			if (current.duration != null)
				try {
					timer.schedule(new TimerTask() {
						@Override
						public void run() {
							context.addIdle(new IdleTask() {
								@Override
								protected boolean runImplementation() {
									queue.poll();
									update(context);
									return false;
								}

								@Override
								protected void destroyed() {

								}
							});
						}
					}, current.duration.toMillis());
				} catch (final IllegalStateException e) {
					// While shutting down
				}
		}
	}

	public void destroy(final Context context) {
		timer.cancel();
	}

	public void removeMessage(final Context context, final BannerMessage message) {
		if (queue.isEmpty())
			return; // TODO implement message destroy cb, extraneous removeMessages unnecessary
		queue.remove(message);
		if (queue.isEmpty())
			timer.purge();
		update(context);
	}
}
