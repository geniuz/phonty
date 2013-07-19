/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /home/bigbn/workspace/Phonty/src/com/phonty/improved/ServiceInterface.aidl
 */
package com.phonty.improved;
// Declare any non-default types here with import statements
/** Example service interface */
public interface ServiceInterface extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.phonty.improved.ServiceInterface
{
private static final java.lang.String DESCRIPTOR = "com.phonty.improved.ServiceInterface";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.phonty.improved.ServiceInterface interface,
 * generating a proxy if needed.
 */
public static com.phonty.improved.ServiceInterface asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.phonty.improved.ServiceInterface))) {
return ((com.phonty.improved.ServiceInterface)iin);
}
return new com.phonty.improved.ServiceInterface.Stub.Proxy(obj);
}
@Override public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_refreshBalance:
{
data.enforceInterface(DESCRIPTOR);
this.refreshBalance();
reply.writeNoException();
return true;
}
case TRANSACTION_refreshDirectionCost:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
this.refreshDirectionCost(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.phonty.improved.ServiceInterface
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
@Override public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
@Override public void refreshBalance() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_refreshBalance, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
@Override public void refreshDirectionCost(java.lang.String balance) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(balance);
mRemote.transact(Stub.TRANSACTION_refreshDirectionCost, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_refreshBalance = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_refreshDirectionCost = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
}
public void refreshBalance() throws android.os.RemoteException;
public void refreshDirectionCost(java.lang.String balance) throws android.os.RemoteException;
}
