package model;

public class ItemStack {
    private OrderItem[] stack;
    private int top;

    public ItemStack(int capacity) {
        this.stack = new OrderItem[capacity];
        this.top = -1;
    }

    public boolean push(OrderItem item) {
        if (top >= stack.length - 1) {
            return false;
        }
        stack[++top] = item;
        return true;
    }

    public OrderItem pop() {
        if (isEmpty()) {
            return null;
        }
        OrderItem removed = stack[top];
        stack[top--] = null;
        return removed;
    }

    public boolean isEmpty() {
        return top == -1;
    }

    public int size() {
        return top + 1;
    }

    public OrderItem[] toArray() {
        OrderItem[] result = new OrderItem[size()];
        for (int i = 0; i <= top; i++) {
            result[i] = stack[i];
        }
        return result;
    }
}
