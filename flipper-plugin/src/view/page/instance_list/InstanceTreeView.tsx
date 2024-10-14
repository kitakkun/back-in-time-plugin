import {DownOutlined} from "@ant-design/icons";
import React, {useMemo} from "react";
import {styled, theme} from "flipper-plugin";
import {Badge, Button, Col, Row, Tree, TreeDataNode, Typography} from "antd";
import {StateHolderType} from "./StateHolderType";
import {InstanceItem, PropertyItem} from "./InstanceListView";
import {RiInstanceFill, RiInstanceLine} from "react-icons/ri";
import {Box} from "@mui/material";
import {History} from "@mui/icons-material";

interface InstanceTreeViewProps {
  instances: InstanceItem[];
  showNonDebuggableProperty: boolean;
  onSelectProperty: (instanceUUID: string, propertyName: string) => void;
  onClickHistory: (instanceUUID: string) => void;
}

const StyledTree = styled(Tree)`
  .ant-tree-switcher {
    background: transparent !important;
  }

  .ant-tree-indent-unit::before {
    border-color: ${theme.textColorPlaceholder} !important;
  }

  .ant-tree-indent-unit::after {
    border-color: ${theme.textColorPlaceholder} !important;
  }

  .ant-tree-switcher-leaf-line::after {
    border-color: ${theme.textColorPlaceholder} !important;
  }

  .ant-tree-switcher-leaf-line::before {
    border-color: ${theme.textColorPlaceholder} !important;
  }
`;

interface MyTreeDataNode extends TreeDataNode {
  nodeType: "instance" | "property";
}

interface InstanceTreeDataNode extends MyTreeDataNode {
  nodeType: "instance";
  uuid: string;
  name: string;
  nameAsProperty?: string;
  stateHolderType: StateHolderType;
}

interface PropertyTreeDataNode extends MyTreeDataNode {
  nodeType: "property";
  instanceUUID: string;
  name: string;
  type: string;
  eventCount: number;
  debuggable: boolean;
}

function isInstanceTreeDataNode(node: any): node is InstanceTreeDataNode {
  return node.nodeType == "instance";
}

function isPropertyTreeDataNode(node: any): node is PropertyTreeDataNode {
  return node.nodeType == "property";
}

export function InstanceTreeView({instances, showNonDebuggableProperty, onSelectProperty, onClickHistory,}: InstanceTreeViewProps) {
  const treeData: TreeDataNode[] = useMemo(() =>
      instances.map((instance) => instanceItemToTreeDataNode(instance, StateHolderType.SUBCLASS, showNonDebuggableProperty)),
    [instances, showNonDebuggableProperty],
  );

  return <StyledTree
    treeData={treeData}
    onSelect={(_, info) => {
      if (isInstanceTreeDataNode(info.node)) {
        onClickHistory(info.node.uuid);
      } else if (isPropertyTreeDataNode(info.node)) {
        onSelectProperty(info.node.instanceUUID, info.node.name);
      }
    }}
    blockNode
    showLine
    switcherIcon={<DownOutlined/>}
    showIcon
    titleRender={(node) => {
      if (isInstanceTreeDataNode(node)) {
        return instanceNodeTitle(node.name, node.uuid, node.stateHolderType, onClickHistory, node.nameAsProperty);
      } else if (isPropertyTreeDataNode(node)) {
        return PropertyNodeTitle(node.name, node.type, node.eventCount)
      }
      return <></>;
    }}
  />;
}

function PropertyNodeTitle(name: string, type: string, eventCount: number) {
  return <Row justify={"space-between"} align={"middle"} style={{padding: theme.space.small}}>
    <Typography.Text>{name}</Typography.Text>
    <Row align={"middle"} gutter={theme.space.medium}>
      <Col><Typography.Text type={"secondary"}>{type}</Typography.Text></Col>
      <Col style={{width: 50, display: "flex"}}>
        <Badge count={eventCount}/>
      </Col>
    </Row>
  </Row>;
}

/**
 * construct tree data node from instance item
 * example:
 * - root(subclass) node
 *   - super class node
 *   - property node(state holder)
 *   - property node(normal)
 *
 * keys:
 * - root(subclass) node: instance.uuid
 * - super class node: instance.uuid/instance.superClassName
 * - property node(state holder): instance.uuid/property.name/property.stateHolderInstance.uuid/...
 * - property node(normal): instance.uuid/property.name
 */
function instanceItemToTreeDataNode(
  instance: InstanceItem,
  stateHolderType: StateHolderType,
  showNonDebuggableProperty: boolean,
  key: string = instance.uuid,
  instanceAsProperty?: PropertyItem,
): InstanceTreeDataNode {
  const filteredProperties = instance.properties.filter((property) => showNonDebuggableProperty || property.debuggable);

  const stateHolderPropertyNodes = filteredProperties
    .filter((property) => property.stateHolderInstance)
    .map((property) =>
      instanceItemToTreeDataNode(
        property.stateHolderInstance!,
        StateHolderType.EXTERNAL,
        showNonDebuggableProperty,
        `${key}/${property.name}`,
        property,
      )
    );

  const normalPropertyNodes = filteredProperties
    .filter((property) => !property.stateHolderInstance)
    .map((property) => normalPropertyTreeNode(property, key, instance.uuid));

  const superClassTreeDataNode = instance.superInstanceItem ? instanceItemToTreeDataNode(
    instance.superInstanceItem,
    StateHolderType.SUPERCLASS,
    showNonDebuggableProperty,
    `${key}/${instance.superClassName}`,
  ) : undefined;

  const children = [...stateHolderPropertyNodes, ...normalPropertyNodes];
  if (superClassTreeDataNode) children.unshift(superClassTreeDataNode);

  const getStyle = (): React.CSSProperties => {
    const baseStyle: React.CSSProperties = {background: theme.backgroundWash};
    const borderStyle: React.CSSProperties = {};

    if (stateHolderType == StateHolderType.SUBCLASS) {
      borderStyle.borderTopRightRadius = theme.borderRadius;
      borderStyle.borderTopLeftRadius = theme.borderRadius;
      if (!instance.superInstanceItem) {
        borderStyle.borderBottomLeftRadius = theme.borderRadius;
        borderStyle.borderBottomRightRadius = theme.borderRadius;
      }
    } else {
      if (!instance.superInstanceItem) {
        borderStyle.borderBottomLeftRadius = theme.borderRadius;
        borderStyle.borderBottomRightRadius = theme.borderRadius;
      }
    }
    return {...baseStyle, ...borderStyle};
  }

  return {
    nodeType: "instance",
    selectable: false,
    key: key,
    style: getStyle(),
    children: children,
    uuid: instance.uuid,
    name: instance.name,
    nameAsProperty: instanceAsProperty?.name,
    stateHolderType: stateHolderType,
  }
}

function instanceNodeTitle(name: string, uuid: string, stateHolderType: StateHolderType, onClickHistory: (instanceUUID: string) => void, nameAsProperty?: string) {
  const label = (stateHolderType == StateHolderType.SUBCLASS) ? uuid :
    (stateHolderType == StateHolderType.SUPERCLASS) ? "super" : `external dependency (${uuid})`;

  const showHistoryButton = stateHolderType != StateHolderType.SUPERCLASS;
  const instanceIcon = stateHolderType == StateHolderType.SUBCLASS ? <RiInstanceFill color={theme.primaryColor}/> :
    stateHolderType == StateHolderType.SUPERCLASS ? <RiInstanceLine color={theme.warningColor}/> : <RiInstanceLine/>;

  return <div style={{padding: theme.space.tiny}}>
    <Row align={"middle"} gutter={theme.space.tiny}>
      <Col style={{display: "flex"}}>{instanceIcon}</Col>
      <Col><Typography.Text type={"secondary"}>{label}</Typography.Text></Col>
    </Row>
    <Row justify={"space-between"} align={"middle"}>
      <Box>
        <Typography.Title level={4}>{name}</Typography.Title>
        {nameAsProperty && <Typography.Text type={"secondary"}>{nameAsProperty}</Typography.Text>}
      </Box>
      {showHistoryButton &&
          <Button
              onClick={(event) => {
                event.stopPropagation();
                onClickHistory(uuid);
              }}
              style={{padding: theme.space.tiny, display: "flex", alignItems: "center", justifyItems: "center"}}
          >
              <History fontSize={"small"}/>History
          </Button>
      }
    </Row>
  </div>;
}

function normalPropertyTreeNode(property: PropertyItem, key: string, instanceUUID: string): PropertyTreeDataNode {
  return {
    nodeType: "property",
    key: `${key}/${property.name}`,
    instanceUUID: instanceUUID,
    name: property.name,
    type: property.type,
    eventCount: property.eventCount,
    debuggable: property.debuggable,
  };
}
