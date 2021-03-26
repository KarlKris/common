package com.li.command;

/**
 * @Description 请求命令信息 模块号和命令号共同指向唯一方法
 * @Author li-yuanwen
 * @Date 2021/3/26 10:38
 */
public class Command {

    /** 模块号 **/
    private final int module;

    /** 命令号 **/
    private final int command;

    public Command(int module, int command) {
        this.module = module;
        this.command = command;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Command)) return false;

        Command command1 = (Command) o;

        if (module != command1.module) return false;
        return command == command1.command;
    }

    @Override
    public int hashCode() {
        int result = module;
        result = 31 * result + command;
        return result;
    }

    @Override
    public String toString() {
        return "Command{" +
                "module=" + module +
                ", command=" + command +
                '}';
    }
}
