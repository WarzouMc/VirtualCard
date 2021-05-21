package fr.warzou.virtualcard.core.modules.link;

import org.jetbrains.annotations.NotNull;

public class Point {

    private final LinkedModule.Face face;
    private final int position;

    public Point(@NotNull LinkedModule.Face face, int position) {
        this.face = face;
        this.position = Math.abs(position);
    }

    public LinkedModule.Face face() {
        return this.face;
    }

    public int position() {
        return this.position;
    }

    @NotNull
    public Point inFront() {
        return inFront(this.position);
    }

    @NotNull
    public Point inFront(int position) {
        if (this.face.ordinal() % 2 == 0)
            return new Point(LinkedModule.Face.values()[this.face.ordinal() + 1], position);
        return new Point(LinkedModule.Face.values()[this.face.ordinal() - 1], position);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Point point = (Point) o;

        if (position != point.position)
            return false;
        return face == point.face;
    }

    @Override
    public int hashCode() {
        int result = face != null ? face.hashCode() : 0;
        result = 31 * result + position;
        return result;
    }

    @Override
    public String toString() {
        return "Point{" +
                "face=" + face +
                ", position=" + position +
                '}';
    }
}
