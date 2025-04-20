package model.tank;

import model.Input;

public class TankInput extends Input {
    public TankParams tankParams;//模型参数

    public TankParams getTankParams() {
        return tankParams;
    }

    public void setTankParams(TankParams tankParams) {
        this.tankParams = tankParams;
    }
}
