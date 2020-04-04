package core.userDefinedTask.manualBuild;

public class ManuallyBuildActionConstructorManager extends AbstractConstructorManager<ManuallyBuildActionConstructor> {

	public static ManuallyBuildActionConstructorManager of() {
		return new ManuallyBuildActionConstructorManager();
	}

	public String addNewConstructor() {
		return addNew(ManuallyBuildActionConstructor.of());
	}
}
