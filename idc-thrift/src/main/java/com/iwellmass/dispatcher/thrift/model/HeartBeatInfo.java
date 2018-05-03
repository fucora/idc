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
public class HeartBeatInfo implements org.apache.thrift.TBase<HeartBeatInfo, HeartBeatInfo._Fields>, java.io.Serializable, Cloneable, Comparable<HeartBeatInfo> {
  private static final org.apache.thrift.protocol.TStruct STRUCT_DESC = new org.apache.thrift.protocol.TStruct("HeartBeatInfo");

  private static final org.apache.thrift.protocol.TField APP_ID_FIELD_DESC = new org.apache.thrift.protocol.TField("appId", org.apache.thrift.protocol.TType.I32, (short)1);
  private static final org.apache.thrift.protocol.TField NODE_CODE_FIELD_DESC = new org.apache.thrift.protocol.TField("nodeCode", org.apache.thrift.protocol.TType.STRING, (short)2);
  private static final org.apache.thrift.protocol.TField IP_FIELD_DESC = new org.apache.thrift.protocol.TField("ip", org.apache.thrift.protocol.TType.STRING, (short)3);
  private static final org.apache.thrift.protocol.TField PORT_FIELD_DESC = new org.apache.thrift.protocol.TField("port", org.apache.thrift.protocol.TType.I32, (short)4);
  private static final org.apache.thrift.protocol.TField TASK_COUNT_FIELD_DESC = new org.apache.thrift.protocol.TField("taskCount", org.apache.thrift.protocol.TType.I32, (short)5);
  private static final org.apache.thrift.protocol.TField NODE_ENV_INFO_FIELD_DESC = new org.apache.thrift.protocol.TField("NodeEnvInfo", org.apache.thrift.protocol.TType.STRUCT, (short)6);

  private static final Map<Class<? extends IScheme>, SchemeFactory> schemes = new HashMap<Class<? extends IScheme>, SchemeFactory>();
  static {
    schemes.put(StandardScheme.class, new HeartBeatInfoStandardSchemeFactory());
    schemes.put(TupleScheme.class, new HeartBeatInfoTupleSchemeFactory());
  }

  public int appId; // required
  public String nodeCode; // required
  public String ip; // required
  public int port; // required
  public int taskCount; // optional
  public NodeEnvInfo NodeEnvInfo; // optional

  /** The set of fields this struct contains, along with convenience methods for finding and manipulating them. */
  public enum _Fields implements org.apache.thrift.TFieldIdEnum {
    APP_ID((short)1, "appId"),
    NODE_CODE((short)2, "nodeCode"),
    IP((short)3, "ip"),
    PORT((short)4, "port"),
    TASK_COUNT((short)5, "taskCount"),
    NODE_ENV_INFO((short)6, "NodeEnvInfo");

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
        case 1: // APP_ID
          return APP_ID;
        case 2: // NODE_CODE
          return NODE_CODE;
        case 3: // IP
          return IP;
        case 4: // PORT
          return PORT;
        case 5: // TASK_COUNT
          return TASK_COUNT;
        case 6: // NODE_ENV_INFO
          return NODE_ENV_INFO;
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
  private static final int __APPID_ISSET_ID = 0;
  private static final int __PORT_ISSET_ID = 1;
  private static final int __TASKCOUNT_ISSET_ID = 2;
  private byte __isset_bitfield = 0;
  private static final _Fields optionals[] = {_Fields.TASK_COUNT,_Fields.NODE_ENV_INFO};
  public static final Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> metaDataMap;
  static {
    Map<_Fields, org.apache.thrift.meta_data.FieldMetaData> tmpMap = new EnumMap<_Fields, org.apache.thrift.meta_data.FieldMetaData>(_Fields.class);
    tmpMap.put(_Fields.APP_ID, new org.apache.thrift.meta_data.FieldMetaData("appId", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.NODE_CODE, new org.apache.thrift.meta_data.FieldMetaData("nodeCode", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.IP, new org.apache.thrift.meta_data.FieldMetaData("ip", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRING)));
    tmpMap.put(_Fields.PORT, new org.apache.thrift.meta_data.FieldMetaData("port", org.apache.thrift.TFieldRequirementType.REQUIRED, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.TASK_COUNT, new org.apache.thrift.meta_data.FieldMetaData("taskCount", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.I32)));
    tmpMap.put(_Fields.NODE_ENV_INFO, new org.apache.thrift.meta_data.FieldMetaData("NodeEnvInfo", org.apache.thrift.TFieldRequirementType.OPTIONAL, 
        new org.apache.thrift.meta_data.FieldValueMetaData(org.apache.thrift.protocol.TType.STRUCT        , "NodeEnvInfo")));
    metaDataMap = Collections.unmodifiableMap(tmpMap);
    org.apache.thrift.meta_data.FieldMetaData.addStructMetaDataMap(HeartBeatInfo.class, metaDataMap);
  }

  public HeartBeatInfo() {
  }

  public HeartBeatInfo(
    int appId,
    String nodeCode,
    String ip,
    int port)
  {
    this();
    this.appId = appId;
    setAppIdIsSet(true);
    this.nodeCode = nodeCode;
    this.ip = ip;
    this.port = port;
    setPortIsSet(true);
  }

  /**
   * Performs a deep copy on <i>other</i>.
   */
  public HeartBeatInfo(HeartBeatInfo other) {
    __isset_bitfield = other.__isset_bitfield;
    this.appId = other.appId;
    if (other.isSetNodeCode()) {
      this.nodeCode = other.nodeCode;
    }
    if (other.isSetIp()) {
      this.ip = other.ip;
    }
    this.port = other.port;
    this.taskCount = other.taskCount;
    if (other.isSetNodeEnvInfo()) {
      this.NodeEnvInfo = other.NodeEnvInfo;
    }
  }

  public HeartBeatInfo deepCopy() {
    return new HeartBeatInfo(this);
  }

  @Override
  public void clear() {
    setAppIdIsSet(false);
    this.appId = 0;
    this.nodeCode = null;
    this.ip = null;
    setPortIsSet(false);
    this.port = 0;
    setTaskCountIsSet(false);
    this.taskCount = 0;
    this.NodeEnvInfo = null;
  }

  public int getAppId() {
    return this.appId;
  }

  public HeartBeatInfo setAppId(int appId) {
    this.appId = appId;
    setAppIdIsSet(true);
    return this;
  }

  public void unsetAppId() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __APPID_ISSET_ID);
  }

  /** Returns true if field appId is set (has been assigned a value) and false otherwise */
  public boolean isSetAppId() {
    return EncodingUtils.testBit(__isset_bitfield, __APPID_ISSET_ID);
  }

  public void setAppIdIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __APPID_ISSET_ID, value);
  }

  public String getNodeCode() {
    return this.nodeCode;
  }

  public HeartBeatInfo setNodeCode(String nodeCode) {
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

  public String getIp() {
    return this.ip;
  }

  public HeartBeatInfo setIp(String ip) {
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

  public HeartBeatInfo setPort(int port) {
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

  public int getTaskCount() {
    return this.taskCount;
  }

  public HeartBeatInfo setTaskCount(int taskCount) {
    this.taskCount = taskCount;
    setTaskCountIsSet(true);
    return this;
  }

  public void unsetTaskCount() {
    __isset_bitfield = EncodingUtils.clearBit(__isset_bitfield, __TASKCOUNT_ISSET_ID);
  }

  /** Returns true if field taskCount is set (has been assigned a value) and false otherwise */
  public boolean isSetTaskCount() {
    return EncodingUtils.testBit(__isset_bitfield, __TASKCOUNT_ISSET_ID);
  }

  public void setTaskCountIsSet(boolean value) {
    __isset_bitfield = EncodingUtils.setBit(__isset_bitfield, __TASKCOUNT_ISSET_ID, value);
  }

  public NodeEnvInfo getNodeEnvInfo() {
    return this.NodeEnvInfo;
  }

  public HeartBeatInfo setNodeEnvInfo(NodeEnvInfo NodeEnvInfo) {
    this.NodeEnvInfo = NodeEnvInfo;
    return this;
  }

  public void unsetNodeEnvInfo() {
    this.NodeEnvInfo = null;
  }

  /** Returns true if field NodeEnvInfo is set (has been assigned a value) and false otherwise */
  public boolean isSetNodeEnvInfo() {
    return this.NodeEnvInfo != null;
  }

  public void setNodeEnvInfoIsSet(boolean value) {
    if (!value) {
      this.NodeEnvInfo = null;
    }
  }

  public void setFieldValue(_Fields field, Object value) {
    switch (field) {
    case APP_ID:
      if (value == null) {
        unsetAppId();
      } else {
        setAppId((Integer)value);
      }
      break;

    case NODE_CODE:
      if (value == null) {
        unsetNodeCode();
      } else {
        setNodeCode((String)value);
      }
      break;

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

    case TASK_COUNT:
      if (value == null) {
        unsetTaskCount();
      } else {
        setTaskCount((Integer)value);
      }
      break;

    case NODE_ENV_INFO:
      if (value == null) {
        unsetNodeEnvInfo();
      } else {
        setNodeEnvInfo((NodeEnvInfo)value);
      }
      break;

    }
  }

  public Object getFieldValue(_Fields field) {
    switch (field) {
    case APP_ID:
      return Integer.valueOf(getAppId());

    case NODE_CODE:
      return getNodeCode();

    case IP:
      return getIp();

    case PORT:
      return Integer.valueOf(getPort());

    case TASK_COUNT:
      return Integer.valueOf(getTaskCount());

    case NODE_ENV_INFO:
      return getNodeEnvInfo();

    }
    throw new IllegalStateException();
  }

  /** Returns true if field corresponding to fieldID is set (has been assigned a value) and false otherwise */
  public boolean isSet(_Fields field) {
    if (field == null) {
      throw new IllegalArgumentException();
    }

    switch (field) {
    case APP_ID:
      return isSetAppId();
    case NODE_CODE:
      return isSetNodeCode();
    case IP:
      return isSetIp();
    case PORT:
      return isSetPort();
    case TASK_COUNT:
      return isSetTaskCount();
    case NODE_ENV_INFO:
      return isSetNodeEnvInfo();
    }
    throw new IllegalStateException();
  }

  @Override
  public boolean equals(Object that) {
    if (that == null)
      return false;
    if (that instanceof HeartBeatInfo)
      return this.equals((HeartBeatInfo)that);
    return false;
  }

  public boolean equals(HeartBeatInfo that) {
    if (that == null)
      return false;

    boolean this_present_appId = true;
    boolean that_present_appId = true;
    if (this_present_appId || that_present_appId) {
      if (!(this_present_appId && that_present_appId))
        return false;
      if (this.appId != that.appId)
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

    boolean this_present_taskCount = true && this.isSetTaskCount();
    boolean that_present_taskCount = true && that.isSetTaskCount();
    if (this_present_taskCount || that_present_taskCount) {
      if (!(this_present_taskCount && that_present_taskCount))
        return false;
      if (this.taskCount != that.taskCount)
        return false;
    }

    boolean this_present_NodeEnvInfo = true && this.isSetNodeEnvInfo();
    boolean that_present_NodeEnvInfo = true && that.isSetNodeEnvInfo();
    if (this_present_NodeEnvInfo || that_present_NodeEnvInfo) {
      if (!(this_present_NodeEnvInfo && that_present_NodeEnvInfo))
        return false;
      if (!this.NodeEnvInfo.equals(that.NodeEnvInfo))
        return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    List<Object> list = new ArrayList<Object>();

    boolean present_appId = true;
    list.add(present_appId);
    if (present_appId)
      list.add(appId);

    boolean present_nodeCode = true && (isSetNodeCode());
    list.add(present_nodeCode);
    if (present_nodeCode)
      list.add(nodeCode);

    boolean present_ip = true && (isSetIp());
    list.add(present_ip);
    if (present_ip)
      list.add(ip);

    boolean present_port = true;
    list.add(present_port);
    if (present_port)
      list.add(port);

    boolean present_taskCount = true && (isSetTaskCount());
    list.add(present_taskCount);
    if (present_taskCount)
      list.add(taskCount);

    boolean present_NodeEnvInfo = true && (isSetNodeEnvInfo());
    list.add(present_NodeEnvInfo);
    if (present_NodeEnvInfo)
      list.add(NodeEnvInfo);

    return list.hashCode();
  }

  @Override
  public int compareTo(HeartBeatInfo other) {
    if (!getClass().equals(other.getClass())) {
      return getClass().getName().compareTo(other.getClass().getName());
    }

    int lastComparison = 0;

    lastComparison = Boolean.valueOf(isSetAppId()).compareTo(other.isSetAppId());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetAppId()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.appId, other.appId);
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
    lastComparison = Boolean.valueOf(isSetTaskCount()).compareTo(other.isSetTaskCount());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetTaskCount()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.taskCount, other.taskCount);
      if (lastComparison != 0) {
        return lastComparison;
      }
    }
    lastComparison = Boolean.valueOf(isSetNodeEnvInfo()).compareTo(other.isSetNodeEnvInfo());
    if (lastComparison != 0) {
      return lastComparison;
    }
    if (isSetNodeEnvInfo()) {
      lastComparison = org.apache.thrift.TBaseHelper.compareTo(this.NodeEnvInfo, other.NodeEnvInfo);
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
    StringBuilder sb = new StringBuilder("HeartBeatInfo(");
    boolean first = true;

    sb.append("appId:");
    sb.append(this.appId);
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
    if (isSetTaskCount()) {
      if (!first) sb.append(", ");
      sb.append("taskCount:");
      sb.append(this.taskCount);
      first = false;
    }
    if (isSetNodeEnvInfo()) {
      if (!first) sb.append(", ");
      sb.append("NodeEnvInfo:");
      if (this.NodeEnvInfo == null) {
        sb.append("null");
      } else {
        sb.append(this.NodeEnvInfo);
      }
      first = false;
    }
    sb.append(")");
    return sb.toString();
  }

  public void validate() throws org.apache.thrift.TException {
    // check for required fields
    // alas, we cannot check 'appId' because it's a primitive and you chose the non-beans generator.
    if (nodeCode == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'nodeCode' was not present! Struct: " + toString());
    }
    if (ip == null) {
      throw new org.apache.thrift.protocol.TProtocolException("Required field 'ip' was not present! Struct: " + toString());
    }
    // alas, we cannot check 'port' because it's a primitive and you chose the non-beans generator.
    // check for sub-struct validity
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

  private static class HeartBeatInfoStandardSchemeFactory implements SchemeFactory {
    public HeartBeatInfoStandardScheme getScheme() {
      return new HeartBeatInfoStandardScheme();
    }
  }

  private static class HeartBeatInfoStandardScheme extends StandardScheme<HeartBeatInfo> {

    public void read(org.apache.thrift.protocol.TProtocol iprot, HeartBeatInfo struct) throws org.apache.thrift.TException {
      org.apache.thrift.protocol.TField schemeField;
      iprot.readStructBegin();
      while (true)
      {
        schemeField = iprot.readFieldBegin();
        if (schemeField.type == org.apache.thrift.protocol.TType.STOP) { 
          break;
        }
        switch (schemeField.id) {
          case 1: // APP_ID
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.appId = iprot.readI32();
              struct.setAppIdIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 2: // NODE_CODE
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.nodeCode = iprot.readString();
              struct.setNodeCodeIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 3: // IP
            if (schemeField.type == org.apache.thrift.protocol.TType.STRING) {
              struct.ip = iprot.readString();
              struct.setIpIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 4: // PORT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.port = iprot.readI32();
              struct.setPortIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 5: // TASK_COUNT
            if (schemeField.type == org.apache.thrift.protocol.TType.I32) {
              struct.taskCount = iprot.readI32();
              struct.setTaskCountIsSet(true);
            } else { 
              org.apache.thrift.protocol.TProtocolUtil.skip(iprot, schemeField.type);
            }
            break;
          case 6: // NODE_ENV_INFO
            if (schemeField.type == org.apache.thrift.protocol.TType.STRUCT) {
              struct.NodeEnvInfo = new NodeEnvInfo();
              struct.NodeEnvInfo.read(iprot);
              struct.setNodeEnvInfoIsSet(true);
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
      if (!struct.isSetAppId()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'appId' was not found in serialized data! Struct: " + toString());
      }
      if (!struct.isSetPort()) {
        throw new org.apache.thrift.protocol.TProtocolException("Required field 'port' was not found in serialized data! Struct: " + toString());
      }
      struct.validate();
    }

    public void write(org.apache.thrift.protocol.TProtocol oprot, HeartBeatInfo struct) throws org.apache.thrift.TException {
      struct.validate();

      oprot.writeStructBegin(STRUCT_DESC);
      oprot.writeFieldBegin(APP_ID_FIELD_DESC);
      oprot.writeI32(struct.appId);
      oprot.writeFieldEnd();
      if (struct.nodeCode != null) {
        oprot.writeFieldBegin(NODE_CODE_FIELD_DESC);
        oprot.writeString(struct.nodeCode);
        oprot.writeFieldEnd();
      }
      if (struct.ip != null) {
        oprot.writeFieldBegin(IP_FIELD_DESC);
        oprot.writeString(struct.ip);
        oprot.writeFieldEnd();
      }
      oprot.writeFieldBegin(PORT_FIELD_DESC);
      oprot.writeI32(struct.port);
      oprot.writeFieldEnd();
      if (struct.isSetTaskCount()) {
        oprot.writeFieldBegin(TASK_COUNT_FIELD_DESC);
        oprot.writeI32(struct.taskCount);
        oprot.writeFieldEnd();
      }
      if (struct.NodeEnvInfo != null) {
        if (struct.isSetNodeEnvInfo()) {
          oprot.writeFieldBegin(NODE_ENV_INFO_FIELD_DESC);
          struct.NodeEnvInfo.write(oprot);
          oprot.writeFieldEnd();
        }
      }
      oprot.writeFieldStop();
      oprot.writeStructEnd();
    }

  }

  private static class HeartBeatInfoTupleSchemeFactory implements SchemeFactory {
    public HeartBeatInfoTupleScheme getScheme() {
      return new HeartBeatInfoTupleScheme();
    }
  }

  private static class HeartBeatInfoTupleScheme extends TupleScheme<HeartBeatInfo> {

    @Override
    public void write(org.apache.thrift.protocol.TProtocol prot, HeartBeatInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol oprot = (TTupleProtocol) prot;
      oprot.writeI32(struct.appId);
      oprot.writeString(struct.nodeCode);
      oprot.writeString(struct.ip);
      oprot.writeI32(struct.port);
      BitSet optionals = new BitSet();
      if (struct.isSetTaskCount()) {
        optionals.set(0);
      }
      if (struct.isSetNodeEnvInfo()) {
        optionals.set(1);
      }
      oprot.writeBitSet(optionals, 2);
      if (struct.isSetTaskCount()) {
        oprot.writeI32(struct.taskCount);
      }
      if (struct.isSetNodeEnvInfo()) {
        struct.NodeEnvInfo.write(oprot);
      }
    }

    @Override
    public void read(org.apache.thrift.protocol.TProtocol prot, HeartBeatInfo struct) throws org.apache.thrift.TException {
      TTupleProtocol iprot = (TTupleProtocol) prot;
      struct.appId = iprot.readI32();
      struct.setAppIdIsSet(true);
      struct.nodeCode = iprot.readString();
      struct.setNodeCodeIsSet(true);
      struct.ip = iprot.readString();
      struct.setIpIsSet(true);
      struct.port = iprot.readI32();
      struct.setPortIsSet(true);
      BitSet incoming = iprot.readBitSet(2);
      if (incoming.get(0)) {
        struct.taskCount = iprot.readI32();
        struct.setTaskCountIsSet(true);
      }
      if (incoming.get(1)) {
        struct.NodeEnvInfo = new NodeEnvInfo();
        struct.NodeEnvInfo.read(iprot);
        struct.setNodeEnvInfoIsSet(true);
      }
    }
  }

}

