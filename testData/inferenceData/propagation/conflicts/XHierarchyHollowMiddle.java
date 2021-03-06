package inferenceData.propagation.conflicts;

import inferenceData.annotations.ExpectNotNull;
import inferenceData.annotations.ExpectNullable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class XHierarchyHollowMiddle {

    public interface Top1 {
        @NotNull @ExpectNullable
        Object m(@Nullable @ExpectNotNull Object x);
    }

    public interface Top2 {
        @NotNull @ExpectNullable
        Object m(@Nullable @ExpectNotNull Object x);
    }

    public interface Middle extends Top1, Top2 {
        @ExpectNullable
        Object m(Object x);
    }

    public interface Leaf1 extends Middle {
        @Nullable
        Object m(Object x);
    }

    public interface Leaf2 extends Middle {
        Object m(@NotNull Object x);
    }

}
