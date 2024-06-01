package utils.hodgepodge.object;

public final class Packer<I> {
    private volatile I packingItem;

    public Packer() { }

    public Packer(I packingItem) {
        this.packingItem = packingItem;
    }

    public I getPackingItem() {
        return packingItem;
    }

    public void setPackingItem(I packingItem) {
        this.packingItem = packingItem;
    }
}
