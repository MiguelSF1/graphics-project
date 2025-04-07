package components;

import game.Component;

public class SpriteRenderer extends Component {
    private boolean firstTime = true;

    @Override
    public void start() {
        System.out.println("start");
    }

    @Override
    public void update(float dt) {
        if (firstTime) {
            System.out.println("update");
            firstTime = false;
        }

    }
}
