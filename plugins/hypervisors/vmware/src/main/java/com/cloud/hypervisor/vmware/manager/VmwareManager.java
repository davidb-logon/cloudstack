// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.hypervisor.vmware.manager;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.cloudstack.framework.config.ConfigKey;

import com.cloud.hypervisor.Hypervisor.HypervisorType;
import com.cloud.hypervisor.vmware.mo.HostMO;
import com.cloud.hypervisor.vmware.util.VmwareContext;
import com.cloud.utils.Pair;
import com.vmware.vim25.ManagedObjectReference;

public interface VmwareManager {
    public final String CONTEXT_STOCK_NAME = "vmwareMgr";

    public static final ConfigKey<Long> s_vmwareNicHotplugWaitTimeout = new ConfigKey<Long>("Advanced", Long.class, "vmware.nic.hotplug.wait.timeout", "15000",
            "Wait timeout (milli seconds) for hot plugged NIC of VM to be detected by guest OS.", false, ConfigKey.Scope.Global);

    public static final ConfigKey<Long> templateCleanupInterval = new ConfigKey<Long>("Advanced", Long.class, "vmware.full.clone.template.cleanup.period", "0",
            "period (in minutes) between the start of template cleanup jobs for vmware full cloned templates.", false, ConfigKey.Scope.Global);

    public static final ConfigKey<Boolean> s_vmwareCleanOldWorderVMs = new ConfigKey<Boolean>("Advanced", Boolean.class, "vmware.clean.old.worker.vms", "false",
            "If a worker vm is older then twice the 'job.expire.minutes' + 'job.cancel.threshold.minutes' , remove it.", true, ConfigKey.Scope.Global);

    static final ConfigKey<String> s_vmwareSearchExcludeFolder = new ConfigKey<String>("Advanced", String.class, "vmware.search.exclude.folders", null,
            "Comma separated list of Datastore Folders to exclude from VMWare search", true, ConfigKey.Scope.Global);

    static final ConfigKey<Integer> s_vmwareOVAPackageTimeout = new ConfigKey<Integer>(Integer.class, "vmware.package.ova.timeout", "Advanced", "3600",
            "Vmware script timeout for ova packaging process", true, ConfigKey.Scope.Global, 1000);

    static final ConfigKey<Boolean> s_vmwareCleanupPortGroups = new ConfigKey<Boolean>("Advanced", Boolean.class, "vmware.cleanup.port.groups",  "false",
            "When set to true, the unused port groups are removed from VMware hypervisor hosts. WARNING: Native VMware HA might not work when enabled.", true, ConfigKey.Scope.Global);

    public static final ConfigKey<Integer> VMWARE_STATS_TIME_WINDOW = new ConfigKey<Integer>("Advanced", Integer.class, "vmware.stats.time.window", "300",
            "VMware interval window (in seconds) to collect metrics. If this is set to less than 20, then default (300 seconds) will be used. The interval used must be enabled in vCenter for this change to work, "
            + "otherwise the collection of metrics will result in an error. Check VMWare docs to know how to enable metrics interval.", true);

    static final ConfigKey<String> VmwareUserVmNicDeviceType = new ConfigKey<String>(
            String.class,
            "vmware.uservm.nic.device.type",
            "Advanced",
            "E1000",
            "Specify the default network device type for user VMs, valid values are E1000, PCNet32, Vmxnet2, Vmxnet3",
            true, ConfigKey.Scope.Global, null, null, null, null, null, ConfigKey.Kind.Select, "E1000,PCNet32,Vmxnet2,Vmxnet3");

    String composeWorkerName();

    String getSystemVMIsoFileNameOnDatastore();

    String getSystemVMDefaultNicAdapterType();

    void prepareSecondaryStorageStore(String strStorageUrl, Long storeId);

    void setupResourceStartupParams(Map<String, Object> params);

    List<ManagedObjectReference> addHostToPodCluster(VmwareContext serviceContext, long dcId, Long podId, Long clusterId, String hostInventoryPath) throws Exception;

    String getManagementPortGroupByHost(HostMO hostMo) throws Exception;

    String getServiceConsolePortGroupName();

    String getManagementPortGroupName();

    Pair<String, Long> getSecondaryStorageStoreUrlAndId(long dcId);

    List<Pair<String, Long>> getSecondaryStorageStoresUrlAndIdList(long dcId);

    File getSystemVMKeyFile();

    VmwareStorageManager getStorageManager();

    void gcLeftOverVMs(VmwareContext context);

    boolean needRecycle(String workerTag);

    Pair<Integer, Integer> getAddiionalVncPortRange();

    boolean beginExclusiveOperation(int timeOutSeconds);

    void endExclusiveOperation();

    boolean getFullCloneFlag();

    Map<String, String> getNexusVSMCredentialsByClusterId(Long clusterId);

    String getPrivateVSwitchName(long dcId, HypervisorType hypervisorType);

    public String getRootDiskController();

    public int getVcenterSessionTimeout();

    boolean isLegacyZone(long dcId);

    public String getDataDiskController();
    boolean hasNexusVSM(Long clusterId);
}
