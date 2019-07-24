package io.jooby.compiler;

import io.jooby.Extension;
import io.jooby.Jooby;
import io.jooby.Reified;
import io.jooby.internal.compiler.ConstructorWriter;
import io.jooby.internal.compiler.TypeDefinition;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.objectweb.asm.Opcodes.ACC_PUBLIC;
import static org.objectweb.asm.Opcodes.ACC_SUPER;
import static org.objectweb.asm.Opcodes.ACC_SYNTHETIC;
import static org.objectweb.asm.Opcodes.ALOAD;
import static org.objectweb.asm.Opcodes.ASTORE;
import static org.objectweb.asm.Opcodes.DUP;
import static org.objectweb.asm.Opcodes.GETFIELD;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;
import static org.objectweb.asm.Opcodes.INVOKESTATIC;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;
import static org.objectweb.asm.Opcodes.NEW;
import static org.objectweb.asm.Opcodes.POP;
import static org.objectweb.asm.Opcodes.RETURN;
import static org.objectweb.asm.Opcodes.V1_8;
import static org.objectweb.asm.Type.getMethodDescriptor;
import static org.objectweb.asm.Type.getType;

public class MvcModuleCompiler {
  private static final Type OBJ = getType(Object.class);
  private static final Type EXTENSION = getType(Extension.class);

  private final String controllerClass;
  private final String moduleClass;
  private final String moduleInternalName;
  private final String moduleJava;

  public MvcModuleCompiler(String controllerClass) {
    this.controllerClass = controllerClass;
    this.moduleClass = this.controllerClass + "$Module";
    this.moduleJava = this.moduleClass + ".java";
    this.moduleInternalName = moduleClass.replace(".", "/");
  }

  public byte[] compile(List<Map.Entry<String, MvcHandlerCompiler>> handlers) throws Exception {
    ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    // public class Controller$methodName implements Route.Handler {
    writer.visit(V1_8, ACC_PUBLIC | ACC_SUPER | ACC_SYNTHETIC, moduleInternalName, null,
        OBJ.getInternalName(),
        new String[]{
            EXTENSION.getInternalName()
        });
    writer.visitSource(moduleJava, null);

    new ConstructorWriter()
        .build(moduleClass, controllerClass, writer);

    install(writer, handlers.stream().map(Map.Entry::getValue).collect(Collectors.toList()));

    return writer.toByteArray();
  }

  private void install(ClassWriter writer,
      List<MvcHandlerCompiler> handlers) throws Exception {
    Method install = Extension.class.getDeclaredMethod("install", Jooby.class);
    MethodVisitor visitor = writer
        .visitMethod(ACC_PUBLIC, install.getName(), Type.getMethodDescriptor(install), null, null);
    visitor.visitParameter("app", 0);
    visitor.visitCode();
    Label sourceStart = new Label();
    visitor.visitLabel(sourceStart);

    //    methodVisitor.visitVarInsn(ALOAD, 1);
    //    methodVisitor.visitLdcInsn("/path");
    //    methodVisitor.visitTypeInsn(NEW, "io/jooby/internal/mvc/MvcHandlerImpl");
    //    methodVisitor.visitInsn(DUP);
    //    methodVisitor.visitVarInsn(ALOAD, 0);
    //    methodVisitor.visitFieldInsn(GETFIELD, "io/jooby/internal/mvc/MvcModule", "provider", "Ljavax/inject/Provider;");
    //    methodVisitor.visitMethodInsn(INVOKESPECIAL, "io/jooby/internal/mvc/MvcHandlerImpl", "<init>", "(Ljavax/inject/Provider;)V", false);
    //    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Jooby", "get", "(Ljava/lang/String;Lio/jooby/Route$Handler;)Lio/jooby/Route;", false);
    //    methodVisitor.visitVarInsn(ASTORE, 2);
    //    methodVisitor.visitVarInsn(ALOAD, 2);
    //    methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/String;"));
    //    methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Route", "setReturnType", "(Ljava/lang/reflect/Type;)Lio/jooby/Route;", false);
    //    methodVisitor.visitInsn(POP);
    //    methodVisitor.visitInsn(RETURN);
    //    methodVisitor.visitMaxs(5, 3);
    //    methodVisitor.visitEnd();
    int varIndex = 2;
    for (MvcHandlerCompiler handler : handlers) {
      visitor.visitVarInsn(ALOAD, 1);
      visitor.visitLdcInsn(handler.getPattern());
      visitor.visitTypeInsn(NEW, handler.getGeneratedInternalClass());
      visitor.visitInsn(DUP);
      visitor.visitVarInsn(ALOAD, 0);
      visitor.visitFieldInsn(GETFIELD, moduleInternalName, "provider",
          "Ljavax/inject/Provider;");
      visitor.visitMethodInsn(INVOKESPECIAL, handler.getGeneratedInternalClass(), "<init>",
          "(Ljavax/inject/Provider;)V", false);
      visitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Jooby", handler.getHttpMethod(),
          "(Ljava/lang/String;Lio/jooby/Route$Handler;)Lio/jooby/Route;", false);
      visitor.visitVarInsn(ASTORE, varIndex);
      visitor.visitVarInsn(ALOAD, varIndex);
      varIndex +=1;
      TypeDefinition returnType = handler.getReturnType();
      if (returnType.isRawType()) {
        visitor.visitLdcInsn(handler.getReturnType().toJvmType());
      } else {
//        methodVisitor = classWriter.visitMethod(ACC_PUBLIC, "install", "(Lio/jooby/Jooby;)V", null, new String[] { "java/lang/Exception" });
//        methodVisitor.visitParameter("app", 0);
//        methodVisitor.visitCode();
//        methodVisitor.visitVarInsn(ALOAD, 1);
//        methodVisitor.visitLdcInsn("/path");
//        methodVisitor.visitTypeInsn(NEW, "io/jooby/internal/mvc/MvcHandlerImpl");
//        methodVisitor.visitInsn(DUP);
//        methodVisitor.visitVarInsn(ALOAD, 0);
//        methodVisitor.visitFieldInsn(GETFIELD, "io/jooby/internal/mvc/MvcModule", "provider", "Ljavax/inject/Provider;");
//        methodVisitor.visitMethodInsn(INVOKESPECIAL, "io/jooby/internal/mvc/MvcHandlerImpl", "<init>", "(Ljavax/inject/Provider;)V", false);
//        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Jooby", "get", "(Ljava/lang/String;Lio/jooby/Route$Handler;)Lio/jooby/Route;", false);
//        methodVisitor.visitVarInsn(ASTORE, 2);
//        methodVisitor.visitVarInsn(ALOAD, 2);
//        methodVisitor.visitLdcInsn(Type.getType("Ljava/util/List;"));
//        methodVisitor.visitInsn(ICONST_1);
//        methodVisitor.visitTypeInsn(ANEWARRAY, "java/lang/reflect/Type");
//        methodVisitor.visitInsn(DUP);
//        methodVisitor.visitInsn(ICONST_0);
//        methodVisitor.visitLdcInsn(Type.getType("Ljava/lang/String;"));
//        methodVisitor.visitInsn(AASTORE);
//        methodVisitor.visitMethodInsn(INVOKESTATIC, "io/jooby/Reified", "getParameterized", "(Ljava/lang/reflect/Type;[Ljava/lang/reflect/Type;)Lio/jooby/Reified;", false);
//        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Reified", "getType", "()Ljava/lang/reflect/Type;", false);
//        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Route", "setReturnType", "(Ljava/lang/reflect/Type;)Lio/jooby/Route;", false);
//        methodVisitor.visitInsn(POP);
//        methodVisitor.visitInsn(RETURN);
//        methodVisitor.visitMaxs(6, 3);
//        methodVisitor.visitEnd();
        visitor.visitLdcInsn(returnType.toJvmType());

        List<TypeDefinition> args = returnType.getArguments();
        visitor.visitInsn(Opcodes.ICONST_0 + args.size());
        visitor.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/reflect/Type");
        for (int i = 0; i < args.size(); i++) {
          visitor.visitInsn(Opcodes.DUP);
          visitor.visitInsn(Opcodes.ICONST_0 + i);
          visitor.visitLdcInsn(args.get(i).toJvmType());
          visitor.visitInsn(Opcodes.AASTORE);
        }
        Method reified = Reified.class.getMethod("getParameterized", java.lang.reflect.Type.class,
            java.lang.reflect.Type[].class);
        visitor.visitMethodInsn(INVOKESTATIC, "io/jooby/Reified", reified.getName(),
            getMethodDescriptor(reified), false);
        Method reifiedToType = Reified.class.getDeclaredMethod("getType");
        visitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Reified", reifiedToType.getName(),
            getMethodDescriptor(reifiedToType), false);
      }
      visitor.visitMethodInsn(INVOKEVIRTUAL, "io/jooby/Route", "setReturnType",
          "(Ljava/lang/reflect/Type;)Lio/jooby/Route;", false);
      visitor.visitInsn(POP);
    }
    visitor.visitInsn(RETURN);
    visitor.visitMaxs(0, 0);
    visitor.visitEnd();
  }
}
