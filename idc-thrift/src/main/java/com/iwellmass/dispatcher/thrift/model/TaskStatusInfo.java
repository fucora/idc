/**
 * Autogenerated by Thrift Compiler (0.9.2)
 *
 * DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
 *  @generated
 */
package com.iwellmass.dispatcher.thrift.model;

import org.apache.thrift.scheme.IScheme;
import org.apache.thrift.scheme.SchemeFactory;
import org.apache.thrift.scheme.StandardScheme;

import org.apache.thrift.scheme.TupleScheme;
import org.apache.thrift.protocol.TTupleProtocol;
import org.apache.thrift.protocol.TProtocolException;
import org.apache.thrift.EncodingUtils;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.server.AbstractNonblockingServer.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.EnumMap;
import java.util.Set;
import java.util.HashSet;
import java.util.EnumSet;
import java.util.Collections;
import java.util.BitSet;
import java.nio.ByteBuffer;
import java.util.Arrays;
import javax.annotation.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"cast", "rawtypes", "serial", "unchecked"})
@Generated(value = "Autogenerated by Thrift Compiler (0.9.2)", date = "2016-5-17")
public class TaskStatusInfo implements org.apache.thrift.TBase<TaskStatusInfo, TaskStatusInfo._Fields>, java.io.Serializable, Cloneable, Comparable<TaskStatusInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("TaskStatusInfo");

  private static final org.apache.thrift.protocol.TField IP_FIELD_DESC = new org.apache.thrift.protocol.TField("ip", org.apache.thrift.protocol.TType.STRING, (short)1);
  private static final org.apache.thrift.protocol.TField PORT_FIELD_DESC = new org.apache.thrift.protocol.TField("port", org.apache.thrift.protocol.TType.I32, (short)2);
  private static final org.apache.thrift.protocol.TField NODE_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("nodeCode", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField TASK_ENTITY_FIELD_DESC = new org.apache.thrift.protocol.TField("taskEntity", org.apache.thrift.protocol.TType.STRUCT, (short)4);
  private static final org.apache.thrift.protocol.TField STATUS_LIST_FIELD_DESC = new org.apache.thrift.protocol.TField("statusList", org.apache.thrift.protocol.TType.LIST, (short)5);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new TaskStatusInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new TaskStatusInfoTupleSchemeFactory());
  }

  public String ip; // required
  public int port; // required
  public String nodeCode; // required
  public TaskEntity taskEntity; // required
  public List<ExecuteStatus> statusList; // required

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    IP((short)1, "ip"),
    PORT((short)2, "port"),
    NODE_CODE((short)3, "nodeCode"),
    TASK_ENTITY((short)4, "taskEntity"),
    STATUS_LIST((short)5, "statusList");

    private static final Map<String, _Fields> byName = new HashMap<String, _Fields>();

    static {
      for (_Fields field : EnumSet.allOf(_Fields.class)) {
        byName.put(field.getFieldName(), field);
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, or null if its not found.
     */
    public static _Fields findByThriftId(int fieldId) {
      switch(fieldId) {
        case 1: // IP
          return IP;
        case 2: // PORT
          return PORT;
        case 3: // NODE_CODE
          return NODE_CODE;
        case 4: // TASK_ENTITY
          return TASK_ENTITY;
        case 5: // STATUS_LIST
          return STATUS_LIST;
        default:
          return null;
      }
    }

    /**
     * Find the _Fields constant that matches fieldId, throwing an exception
     * if it is not found.
     */
    public static _Fields findByThriftIdOrThrow(int fieldId) {
      _Fields fields = findByThriftId(fieldId);
      if (fields == null) throw new IllegalArgumentException("Field " + fieldId + " doesn't exist!");
      return fields;
    }

    /**
     * Find the _Fields constant that matches name, or null if its not found.
     */
    public static _Fields findByName(String name) {
      return byName.get(name);
    }

    private final short _thriftId;
    private final String _fieldName;

    _Fields(short thriftId, String fieldName) {
      _thriftId = thriftId;
      _fieldName = fieldName;
    }

    public short getThriftFieldId() {
      return _thriftId;
    }

    public String getFieldName() {
      return _fieldName;
    }
  }

  // isset id assignments
  private static final int __PORT_ISSET_ID = 0;
  private byte __isset_bitfield = 0;
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.IP, new org.apache.thrift.meta_data.FieldMetaData("ip", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PORT, new org.apache.thrift.meta_data.FieldMetaData("port", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.NODE_CODE, new org.apache.thrift.meta_data.FieldMetaData("nodeCode", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.TASK_ENTITY, new org.apache.thrift.meta_data.FieldMetaData("taskEntity", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, TaskEntity.class)));
    tmpMap.put(_Fields.STATUS_LIST, new org.apache.thrift.meta_data.FieldMetaData("statusList", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.ListMetaData(org.apache.thrift.protocol.TType.LIST, 
            new org.apache.thrift.meta_data.StructMetaData(org.apache.thrift.protocol.TType.STRUCT, ExecuteStatus.class))));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(TaskStatusInfo.class, metaDataMap);
  }

  public TaskStatusInfo() {
  }

  public TaskStatusInfo(
    String ip,
    int port,
    String nodeCode,
    TaskEntity taskEntity,
    List<ExecuteStatus> statusList)
  {
    this();
    this.ip = ip;
    this.port = port;
    setPortIsSet(true);
    this.nodeCode = nodeCode;
    this.taskEntity = taskEntity;
    this.statusList = statusList;
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public TaskStatusInfo(TaskStatusInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    if (other.isSetIp()) {
      this.ip = other.ip;
    }
    this.port = other.port;
    if (other.isSetNodeCode()) {
      this.nodeCode = other.nodeCode;
    }
    if (other.isSetTaskEntity()) {
      this.taskEntity = new TaskEntity(other.taskEntity);
    }
    if (other.isSetStatusList()) {
      List<ExecuteStatus> __this__statusList = new ArrayList<ExecuteStatus>(other.statusList.size());
      for (ExecuteStatus other_element : other.statusList) {
        __this__statusList.add(new ExecuteStatus(other_element));
      }
      this.statusList = __this__statusList;
    }
  }

  public TaskStatusInfo deepCopy() {
    return new TaskStatusInfo(this);
  }

  @Override
  public void clear() {
    this.ip = null;
    setPortIsSet(false);
    this.port = 0;
    this.nodeCode = null;
    this.taskEntity = null;
    this.statusList = null;
  }

  public String getIp() {
    return this.ip;
  }

  public TaskStatusInfo setIp(String ip) {
    this.ip = ip;
    return this;
  }

  public void unsetIp() {
    this.ip = null;
  }

  /** Returns true if field ip is set (has been assigned a value) and false otherwise */
  public boolean isSetIp() {
    return this.ip != null;
  }

  public void setIpIsSet(boolean value) {
    if (!value) {
      this.ip = null;
    }
  }

  public int getPort() {
    return this.port;
  }

  public TaskStatusInfo setPort(int port) {
    this.port = port;
    setPortIsSet(true);
    return this;
  }

  public void unsetPort() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __PORT_ISSET_ID);
  }

  /** Returns true if field port is set (has been assigned a value) and false otherwise */
  public boolean isSetPort() {
    return EncodingUtils.testBit(__isset_bitfield, __PORT_ISSET_ID);
  }

  public void setPortIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __PORT_ISSET_ID, value);
  }

  public String getNodeCode() {
    return this.nodeCode;
  }

  public TaskStatusInfo setNodeCode(String nodeCode) {
    this.nodeCode = nodeCode;
    return this;
  }

  public void unsetNodeCode() {
    this.nodeCode = null;
  }

  /** Returns true if field nodeCode is set (has been assigned a value) and false otherwise */
  public boolean isSetNodeCode() {
    return this.nodeCode != null;
  }

  public void setNodeCodeIsSet(boolean value) {
    if (!value) {
      this.nodeCode = null;
    }
  }

  public TaskEntity getTaskEntity() {
    return this.taskEntity;
  }

  public TaskStatusInfo setTaskEntity(TaskEntity taskEntity) {
    this.taskEntity = taskEntity;
    return this;
  }

  public void unsetTaskEntity() {
    this.taskEntity = null;
  }

  /** Returns true if field taskEntity is set (has been assigned a value) and false otherwise */
  public boolean isSetTaskEntity() {
    return this.taskEntity != null;
  }

  public void setTaskEntityIsSet(boolean value) {
    if (!value) {
      this.taskEntity = null;
    }
  }

  public int getStatusListSize() {
    return (this.statusList == null) ? 0 : this.statusList.size();
  }

  public java.util.Iterator<ExecuteStatus> getStatusListIterator() {
    return (this.statusList == null) ? null : this.statusList.iterator();
  }

  public void addToStatusList(ExecuteStatus elem) {
    if (this.statusList == null) {
      this.statusList = new ArrayList<ExecuteStatus>();
    }
    this.statusList.add(elem);
  }

  public List<ExecuteStatus> getStatusList() {
    return this.statusList;
  }

  public TaskStatusInfo setStatusList(List<ExecuteStatus> statusList) {
    this.statusList = statusList;
    return this;
  }

  public void unsetStatusList() {
    this.statusList = null;
  }

  /** Returns true if field statusList is set (has been assigned a value) and false otherwise */
  public boolean isSetStatusList() {
    return this.statusList != null;
  }

  public void setStatusListIsSet(boolean value) {
    if (!value) {
      this.statusList = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case IP:
      if (value == null) {
        unsetIp();
      } else {
        setIp((String)value);
      }
      break;

    case PORT:
      if (value == null) {
        unsetPort();
      } else {
        setPort((Integer)value);
      }
      break;

    case NODE_CODE:
      if (value == null) {
        unsetNodeCode();
      } else {
        setNodeCode((String)value);
      }
      break;

    case TASK_ENTITY:
      if (value == null) {
        unsetTaskEntity();
      } else {
        setTaskEntity((TaskEntity)value);
      }
      break;

    case STATUS_LIST:
      if (value == null) {
        unsetStatusList();
      } else {
        setStatusList((List<ExecuteStatus>)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case IP:
      return getIp();

    case PORT:
      return Integer.valueOf(getPort());

    case NODE_CODE:
      return getNodeCode();

    case TASK_ENTITY:
      return getTaskEntity();

    case STATUS_LIST:
      return getStatusList();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case IP:
      return isSetIp();
    case PORT:
      return isSetPort();
    case NODE_CODE:
      return isSetNodeCode();
    case TASK_ENTITY:
      return isSetTaskEntity();
    case STATUS_LIST:
      return isSetStatusList();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof TaskStatusInfo)
      return this.equals((TaskStatusInfo)that);
    return false;
  }

  public boolean equals(TaskStatusInfo that) {
    if (that == null)
      return false;

    boolean this_present_ip = true && this.isSetIp();
    boolean that_present_ip = true && that.isSetIp();
    if (this_present_ip || that_present_ip) {
      if (!(this_present_ip && that_present_ip))
        return false;
      if (!this.ip.equals(that.ip))
        return false;
    }

    boolean this_present_port = true;
    boolean that_present_port = true;
    if (this_present_port || that_present_port) {
      if (!(this_present_port && that_present_port))
        return false;
      if (this.port != that.port)
        return false;
    }

    boolean this_present_nodeCode = true && this.isSetNodeCode();
    boolean that_present_nodeCode = true && that.isSetNodeCode();
    if (this_present_nodeCode || that_present_nodeCode) {
      if (!(this_present_nodeCode && that_present_nodeCode))
        return false;
      if (!this.nodeCode.equals(that.nodeCode))
        return false;
    }

    boolean this_present_taskEntity = true && this.isSetTaskEntity();
    boolean that_present_taskEntity = true && that.isSetTaskEntity();
    if (this_present_taskEntity || that_present_taskEntity) {
      if (!(this_present_taskEntity && that_present_taskEntity))
        return false;
      if (!this.taskEntity.equals(that.taskEntity))
        return false;
    }

    boolean this_present_statusList = true && this.isSetStatusList();
    boolean that_present_statusList = true && that.isSetStatusList();
    if (this_present_statusList || that_present_statusList) {
      if (!(this_present_statusList && that_present_statusList))
        return false;
      if (!this.statusList.equals(that.statusList))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_ip = true && (isSetIp());
    list.add(present_ip);
    if (present_ip)
      list.add(ip);

    boolean present_port = true;
    list.add(present_port);
    if (present_port)
      list.add(port);

    boolean present_nodeCode = true && (isSetNodeCode());
    list.add(present_nodeCode);
    if (present_nodeCode)
      list.add(nodeCode);

    boolean present_taskEntity = true && (isSetTaskEntity());
    list.add(present_taskEntity);
    if (present_taskEntity)
      list.add(taskEntity);

    boolean present_statusList = true && (isSetStatusList());
    list.add(present_statusList);
    if (present_statusList)
      list.add(statusList);

    return list.hashCode();
  }

  @Override
  public int compareTo(TaskStatusInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetIp()).compareTo(other.isSetIp());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetIp()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.ip, other.ip);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetPort()).compareTo(other.isSetPort());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetPort()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.port, other.port);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNodeCode()).compareTo(other.isSetNodeCode());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNodeCode()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.nodeCode, other.nodeCode);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetTaskEntity()).compareTo(other.isSetTaskEntity());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaskEntity()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taskEntity, other.taskEntity);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetStatusList()).compareTo(other.isSetStatusList());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetStatusList()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.statusList, other.statusList);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    return 0;
  }

  public _Fields fieldForId(int fieldId) {
    return _Fields.findByThriftId(fieldId);
  }

  public void read(org.apache.thrift.protocol.TProtocol iprot) throws org.apache.thrift.TException {
    schemes.get(iprot.getScheme()).getScheme().read(iprot, this);
  }

  public void write(org.apache.thrift.protocol.TProtocol oprot) throws org.apache.thrift.TException {
    schemes.get(oprot.getScheme()).getScheme().write(oprot, this);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("TaskStatusInfo(");
    boolean first = true;

    sb.append("ip:");
    if (this.ip == null) {
      sb.append("null");
    } else {
      sb.append(this.ip);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("port:");
    sb.append(this.port);
    first = false;
    if (!first) sb.append(", ");
    sb.append("nodeCode:");
    if (this.nodeCode == null) {
      sb.append("null");
    } else {
      sb.append(this.nodeCode);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("taskEntity:");
    if (this.taskEntity == null) {
      sb.append("null");
    } else {
      sb.append(this.taskEntity);
    }
    first = false;
    if (!first) sb.append(", ");
    sb.append("statusList:");
    if (this.statusList == null) {
      sb.append("null");
    } else {
      sb.append(this.statusList);
    }
    first = false;
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    if (ip == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'ip' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'port' because it's a primitive and you chose the non-beans generator.
    if (nodeCode == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'nodeCode' was not present! Struct: " + toString());
    }
    if (taskEntity == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'taskEntity' was not present! Struct: " + toString());
    }
    if (statusList == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'statusList' was not present! Struct: " + toString());
    }
    // check for sub-struct validity
    if (taskEntity != null) {
      taskEntity.validate();
    }
  }

  private void writeObject(java.io.ObjectOutputStream out) throws java.io.IOException {
    try {
      write(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(out)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private void readObject(java.io.ObjectInputStream in) throws java.io.IOException, ClassNotFoundException {
    try {
      // it doesn't seem like you should have to do this, but java serialization is wacky, and doesn't call the default constructor.
      __isset_bitfield = 0;
      read(new org.apache.thrift.protocol.TCompactProtocol(new org.apache.thrift.transport.TIOStreamTransport(in)));
    } catch (org.apache.thrift.TException te) {
      throw new java.io.IOException(te);
    }
  }

  private static class TaskStatusInfoStandardSchemeFactory implements SchemeFactory {
    public TaskStatusInfoStandardScheme getScheme() {
      return new TaskStatusInfoStandardScheme();
    }
  }

  private static class TaskStatusInfoStandardScheme extends StandardScheme<TaskStatusInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, TaskStatusInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // IP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.ip = iprot.readString();
              struct.setIpIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // PORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.port = iprot.readI32();
              struct.setPortIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // NODE_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.nodeCode = iprot.readString();
              struct.setNodeCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // TASK_ENTITY
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.taskEntity = new TaskEntity();
              struct.taskEntity.read(iprot);
              struct.setTaskEntityIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // STATUS_LIST
            if (schemeField.type == org.apache.thrift.protocol.TType.LIST) {
              {
                org.apache.thrift.protocol.TList _list0 = iprot.readListBegin();
                struct.statusList = new ArrayList<ExecuteStatus>(_list0.size);
                ExecuteStatus _elem1;
                for (int _i2 = 0; _i2 < _list0.size; ++_i2)
                {
                  _elem1 = new ExecuteStatus();
                  _elem1.read(iprot);
                  struct.statusList.add(_elem1);
                }
                iprot.readListEnd();
              }
              struct.setStatusListIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          default:
            org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
        }
        iprot.readFieldEnd();
      }
      iprot.readStructEnd();

      // check for required fields of primitive type, which can't be checked in the validate method
      if (!struct.isSetPort()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'port' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, TaskStatusInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      if (struct.ip != null) {
        oprot.writeFieldBegin(IP_FIELD_DESC);
        oprot.writeString(struct.ip);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PORT_FIELD_DESC);
      oprot.writeI32(struct.port);
      oprot.writeFieldEnd();
      if (struct.nodeCode != null) {
        oprot.writeFieldBegin(NODE_CODE_FIELD_DESC);
        oprot.writeString(struct.nodeCode);
        oprot.writeFieldEnd();
      }
      if (struct.taskEntity != null) {
        oprot.writeFieldBegin(TASK_ENTITY_FIELD_DESC);
        struct.taskEntity.write(oprot);
        oprot.writeFieldEnd();
      }
      if (struct.statusList != null) {
        oprot.writeFieldBegin(STATUS_LIST_FIELD_DESC);
        {
          oprot.writeListBegin(new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, struct.statusList.size()));
          for (ExecuteStatus _iter3 : struct.statusList)
          {
            _iter3.write(oprot);
          }
          oprot.writeListEnd();
        }
        oprot.writeFieldEnd();
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class TaskStatusInfoTupleSchemeFactory implements SchemeFactory {
    public TaskStatusInfoTupleScheme getScheme() {
      return new TaskStatusInfoTupleScheme();
    }
  }

  private static class TaskStatusInfoTupleScheme extends TupleScheme<TaskStatusInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, TaskStatusInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeString(struct.ip);
      oprot.writeI32(struct.port);
      oprot.writeString(struct.nodeCode);
      struct.taskEntity.write(oprot);
      {
        oprot.writeI32(struct.statusList.size());
        for (ExecuteStatus _iter4 : struct.statusList)
        {
          _iter4.write(oprot);
        }
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, TaskStatusInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.ip = iprot.readString();
      struct.setIpIsSet(true);
      struct.port = iprot.readI32();
      struct.setPortIsSet(true);
      struct.nodeCode = iprot.readString();
      struct.setNodeCodeIsSet(true);
      struct.taskEntity = new TaskEntity();
      struct.taskEntity.read(iprot);
      struct.setTaskEntityIsSet(true);
      {
        org.apache.thrift.protocol.TList _list5 = new org.apache.thrift.protocol.TList(org.apache.thrift.protocol.TType.STRUCT, iprot.readI32());
        struct.statusList = new ArrayList<ExecuteStatus>(_list5.size);
        ExecuteStatus _elem6;
        for (int _i7 = 0; _i7 < _list5.size; ++_i7)
        {
          _elem6 = new ExecuteStatus();
          _elem6.read(iprot);
          struct.statusList.add(_elem6);
        }
      }
      struct.setStatusListIsSet(true);
    }
  }

}

