/**
 * SUBCLASS: instance which is the target of the debug (top of the tree)
 * SUPERCLASS: instance which is the super class of the target instance or some other super class
 * EXTERNAL: instance which is referenced via the property of the target instance
 */
export enum StateHolderType {
  SUBCLASS = "SUBCLASS",
  SUPERCLASS = "SUPERCLASS",
  EXTERNAL = "EXTERNAL",
}
