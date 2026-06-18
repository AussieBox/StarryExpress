package org.aussiebox.starexpress.client.gui.owo;

import io.wispforest.owo.ui.component.LabelComponent;
import io.wispforest.owo.ui.component.TextBoxComponent;
import io.wispforest.owo.ui.container.Containers;
import io.wispforest.owo.ui.container.FlowLayout;
import io.wispforest.owo.ui.container.OverlayContainer;
import io.wispforest.owo.ui.core.*;
import io.wispforest.owo.ui.util.Delta;
import io.wispforest.owo.ui.util.UISounds;
import io.wispforest.owo.util.EventSource;
import io.wispforest.owo.util.EventStream;
import net.minecraft.text.Text;
import net.minecraft.util.Colors;
import net.minecraft.util.math.RotationAxis;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class CollapsibleTextBoxComponent extends FlowLayout {
    protected final ExpandableTextBoxComponent textBox;
    protected final FlowLayout contentLayout;
    protected final OverlayContainer<FlowLayout> overlay;

    protected final EventStream<OnToggled> toggledEvents = OnToggled.newStream();

    protected final List<Component> collapsibleChildren = new ArrayList<>();
    protected final List<Component> collapsibleChildrenView = Collections.unmodifiableList(this.collapsibleChildren);
    protected boolean expanded;

    public CollapsibleTextBoxComponent(Sizing horizontalSizing, Sizing verticalSizing, TextBoxComponent textBox, boolean expanded) {
        super(horizontalSizing, Sizing.content(), Algorithm.VERTICAL);
        this.textBox = new ExpandableTextBoxComponent(textBox);
        this.contentLayout = Containers.verticalFlow(Sizing.expand(), verticalSizing);
        this.expanded = expanded;

        this.textBox.overlay.mouseDown().subscribe((mouseX, mouseY, button) -> {
            this.toggleExpansion();
            UISounds.playInteractionSound();
            return true;
        });

        this.textBox.overlay.child().targetRotation = expanded ? 90 : 0;
        this.textBox.overlay.child().rotation = this.textBox.overlay.child().targetRotation;
        this.textBox.overlay.surface(Surface.flat(Colors.BLACK).and(Surface.outline(0xFFA0A0A0)));

        super.child(this.textBox);

        overlay = Containers.overlay(this.contentLayout);
        overlay.sizing(Sizing.expand(), Sizing.content());
        overlay.surface(Surface.BLANK);
        overlay.positioning(Positioning.absolute(0, 26));

        super.child(overlay);

        super.allowOverflow(true);
        this.textBox.allowOverflow(true);
        this.contentLayout.allowOverflow(true);
    }

    public void toggleExpansion() {
        if (expanded) {
            this.contentLayout.clearChildren();
            this.textBox.overlay.child().targetRotation = 0;
        } else {
            this.contentLayout.children(this.collapsibleChildren);
            this.textBox.overlay.child().targetRotation = 90;
        }

        this.expanded = !this.expanded;
        this.toggledEvents.sink().onToggle(this.expanded);
    }

    public List<Component> collapsibleChildren() {
        return this.collapsibleChildrenView;
    }

    public boolean expanded() {
        return this.expanded;
    }

    public EventSource<OnToggled> onToggled() {
        return this.toggledEvents.source();
    }

    @Override
    public boolean canFocus(FocusSource source) {
        return source == FocusSource.KEYBOARD_CYCLE;
    }

    @Override
    public boolean onKeyPress(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_SPACE || keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER) {
            this.toggleExpansion();
            UISounds.playInteractionSound();

            super.onKeyPress(keyCode, scanCode, modifiers);
            return true;
        }

        return super.onKeyPress(keyCode, scanCode, modifiers);
    }

    @Override
    public FlowLayout child(Component child) {
        this.collapsibleChildren.add(child);
        if (this.expanded) this.contentLayout.child(child);
        return this;
    }

    @Override
    public FlowLayout children(Collection<? extends Component> children) {
        this.collapsibleChildren.addAll(children);
        if (this.expanded) this.contentLayout.children(children);
        return this;
    }

    @Override
    public FlowLayout child(int index, Component child) {
        this.collapsibleChildren.add(index, child);
        if (this.expanded) this.contentLayout.child(index, child);
        return this;
    }

    @Override
    public FlowLayout children(int index, Collection<? extends Component> children) {
        this.collapsibleChildren.addAll(index, children);
        if (this.expanded) this.contentLayout.children(index, children);
        return this;
    }

    @Override
    public FlowLayout removeChild(Component child) {
        this.collapsibleChildren.remove(child);
        return this.contentLayout.removeChild(child);
    }

    @Override
    public boolean isInBoundingBox(double x, double y) {
        if (this.expanded && overlay.isInBoundingBox(x, y)) return true;
        return super.isInBoundingBox(x, y);
    }

    public interface OnToggled {
        void onToggle(boolean nowExpanded);

        static EventStream<OnToggled> newStream() {
            return new EventStream<>(subscribers -> nowExpanded -> {
                for (var subscriber : subscribers) {
                    subscriber.onToggle(nowExpanded);
                }
            });
        }
    }

    protected static class ExpandableTextBoxComponent extends FlowLayout {
        public final TextBoxComponent textBox;
        public final OverlayContainer<SpinnyBoiComponent> overlay;

        protected ExpandableTextBoxComponent(TextBoxComponent textBox) {
            super(Sizing.content(), Sizing.content(), Algorithm.HORIZONTAL);
            this.textBox = textBox;
            this.overlay = Containers.overlay(new SpinnyBoiComponent());

            this.overlay.closeOnClick(false);
            this.overlay.positioning(Positioning.relative(101, 80));
            this.overlay.alignment(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
            this.overlay.sizing(Sizing.fixed(20), Sizing.fixed(20));
            this.overlay.zIndex(this.textBox.zIndex()+1);

            super.child(this.textBox);
            super.child(this.overlay);
        }

        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            if (textBox.isFocused()) overlay.surface(Surface.flat(Colors.BLACK).and(Surface.outline(0xFFFFFFFF)));
            else overlay.surface(Surface.flat(Colors.BLACK).and(Surface.outline(0xFFA0A0A0)));

            super.draw(context, mouseX, mouseY, partialTicks, delta);
        }
    }

    protected static class SpinnyBoiComponent extends LabelComponent {
        protected float rotation = 90;
        protected float targetRotation = 90;

        public SpinnyBoiComponent() {
            super(Text.literal(">"));
            this.cursorStyle(CursorStyle.HAND);
            super.positioning(Positioning.relative(50, 50));
            super.margins(Insets.of(0, -1, 0, -3));
        }

        @Override
        public void update(float delta, int mouseX, int mouseY) {
            super.update(delta, mouseX, mouseY);
            this.rotation += (float) Delta.compute(this.rotation, this.targetRotation, delta * .65);
        }

        @Override
        public void draw(OwoUIDrawContext context, int mouseX, int mouseY, float partialTicks, float delta) {
            var matrices = context.getMatrices();

            matrices.push();
            matrices.translate(this.x + this.width / 2f - 1, this.y + this.height / 2f - 1, 0);
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(this.rotation));
            matrices.translate(-(this.x + this.width / 2f - 1), -(this.y + this.height / 2f - 1), 0);

            super.draw(context, mouseX, mouseY, partialTicks, delta);
            matrices.pop();
        }
    }
}
