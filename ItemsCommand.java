
public class ItemsCommand extends Command {

	@Override
	public boolean execute(Player player) {
		GameEngine.gui.println(player.getLongInventory());
		return false;
	}

}
