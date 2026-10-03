class Ball {
    PApplet g;
    float x, y;
    int col;

    Ball(PApplet g, float x, float y) {
        this.g = g;
        this.x = x;
        this.y = y;
    }

    void setColor(int c) {
        col = c;
    }

    void draw() {
        g.noStroke();
        g.fill(col);
        g.circle(x, y, 30);
    }
}
