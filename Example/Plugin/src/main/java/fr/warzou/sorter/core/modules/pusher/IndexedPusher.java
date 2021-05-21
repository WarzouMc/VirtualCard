package fr.warzou.sorter.core.modules.pusher;

import fr.warzou.virtualcard.api.events.events.pusher.PusherPushEvent;
import fr.warzou.virtualcard.core.modules.Item;
import fr.warzou.virtualcard.core.modules.container.AbstractContainer;
import fr.warzou.virtualcard.core.modules.link.LinkedModule;
import fr.warzou.virtualcard.core.modules.link.Point;
import fr.warzou.virtualcard.core.modules.pusher.Pusher;
import fr.warzou.virtualcard.utils.event.EventsManager;
import fr.warzou.virtualcard.utils.module.ModuleBase;
import fr.warzou.virtualcard.utils.module.stream.executor.reader.Reader;
import fr.warzou.virtualcard.utils.module.stream.executor.writer.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class IndexedPusher extends Pusher.AbstractIndexedPusher {

    public IndexedPusher(String subName, String path) {
        super(subName, path);
    }

    @Override
    public AbstractContainer linkTo() {
        Reader<String> reader = this.stream.readString(this.name + ".out.stats.link");
        Optional<String> optional = reader.read();
        reader.close();
        if (!optional.isPresent())
            return null;

        Optional<ModuleBase<?>> optionalModuleBase = this.card.getModuleManager().getModule(optional.get());
        if (!optionalModuleBase.isPresent())
            return null;

        ModuleBase<?> moduleBase = optionalModuleBase.get();
        return !(moduleBase instanceof AbstractContainer) ? null : (AbstractContainer) moduleBase;
    }

    @Override
    public void update() {
        ArrayList tasks = getTasks();
        int removeTask = -1;
        for (int i = 0; i < tasks.size(); i++) {
            int task = Math.toIntExact(Math.round(Double.parseDouble(tasks.get(i).toString())));
            if (task == 0) {
                removeTask = i;
                continue;
            }
            tasks.set(i, task - 1);
        }
        if (removeTask >= 0) {
            push();
            tasks.remove(removeTask);
        }
        Writer<ArrayList> writer = this.stream.write(this.name + ".in.push.tasks", ArrayList.class);
        writer.write(tasks);
        writer.push();
        writer.close();
    }

    @Override
    protected void push() {
        AbstractContainer container = linkTo();
        if (container == null)
            return;

        Optional<LinkedModule> optionalLinkedModule = container.linkedModuleFromModule(this);
        if (!optionalLinkedModule.isPresent())
            return;

        LinkedModule linkedModule = optionalLinkedModule.get();
        Point thisPoint = linkedModule.getPoint();
        Point frontPoint = thisPoint.inFront();
        Optional<LinkedModule> optionalFrontLinkedModule = container.linkedModuleFromPoint(frontPoint);
        if (!optionalFrontLinkedModule.isPresent())
            return;

        LinkedModule frontModule = optionalFrontLinkedModule.get();
        ModuleBase<?> moduleBase = frontModule.getModuleBase();
        if (!(moduleBase instanceof AbstractContainer))
            return;

        AbstractContainer frontContainer = (AbstractContainer) moduleBase;
        List<Item<?>> content = container.content();
        int pushPosition = thisPoint.position();
        if (pushPosition >= content.size())
            return;

        Item<?> item = content.get(pushPosition);
        if (item.getItem() == null)
            return;

        frontContainer.inject(item);
        container.eject(item);
        EventsManager eventManager = this.card.getEventManager();
        eventManager.callEvent(new PusherPushEvent(this, item, container, frontContainer));
    }

    @Override
    public String toString() {
        return "IndexedPusher{" +
                "name: " + this.name +
                "}";
    }
}
