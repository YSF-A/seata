/*
 *  Copyright 1999-2019 Seata.io Group.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package io.seata.core.rpc.netty;

import javax.net.ssl.SSLException;
import java.io.File;

import io.netty.channel.Channel;
import io.netty.handler.ssl.ClientAuth;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.seata.common.util.StringUtils;
import io.seata.core.constants.ConfigurationKeys;
import io.seata.core.rpc.TransportServerType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.seata.common.DefaultValues.DEFAULT_ENABLE_CLIENT_BATCH_SEND_REQUEST;
import static io.seata.common.DefaultValues.DEFAULT_RPC_RM_REQUEST_TIMEOUT;
import static io.seata.common.DefaultValues.DEFAULT_RPC_TM_REQUEST_TIMEOUT;
import static io.seata.common.DefaultValues.DEFAULT_SELECTOR_THREAD_PREFIX;
import static io.seata.common.DefaultValues.DEFAULT_SELECTOR_THREAD_SIZE;
import static io.seata.common.DefaultValues.DEFAULT_WORKER_THREAD_PREFIX;
import static io.seata.common.DefaultValues.DEFAULT_CLIENT_ENABLE_TLS;

/**
 * The type Netty client config.
 *
 * @author slievrly
 */
public class NettyClientConfig extends NettyBaseConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(NettyClientConfig.class);

    private int connectTimeoutMillis = 10000;
    private int clientSocketSndBufSize = 153600;
    private int clientSocketRcvBufSize = 153600;
    private int clientWorkerThreads = WORKER_THREAD_SIZE;
    private final Class<? extends Channel> clientChannelClazz = CLIENT_CHANNEL_CLAZZ;
    private int perHostMaxConn = 2;
    private static final int PER_HOST_MIN_CONN = 2;
    private int pendingConnSize = Integer.MAX_VALUE;
    private static final long RPC_RM_REQUEST_TIMEOUT = CONFIG.getLong(ConfigurationKeys.RPC_RM_REQUEST_TIMEOUT, DEFAULT_RPC_RM_REQUEST_TIMEOUT);
    private static final long RPC_TM_REQUEST_TIMEOUT = CONFIG.getLong(ConfigurationKeys.RPC_TM_REQUEST_TIMEOUT, DEFAULT_RPC_TM_REQUEST_TIMEOUT);
    private static String vgroup;
    private static String clientAppName;
    private static int clientType;
    private static int maxInactiveChannelCheck = 10;
    private static final int MAX_NOT_WRITEABLE_RETRY = 2000;
    private static final int MAX_CHECK_ALIVE_RETRY = 300;
    private static final int CHECK_ALIVE_INTERVAL = 10;
    private static final String SOCKET_ADDRESS_START_CHAR = "/";
    private static final long MAX_ACQUIRE_CONN_MILLS = 60 * 1000L;
    private static final String RPC_DISPATCH_THREAD_PREFIX = "rpcDispatch";
    private static final int DEFAULT_MAX_POOL_ACTIVE = 1;
    private static final int DEFAULT_MIN_POOL_IDLE = 0;
    private static final boolean DEFAULT_POOL_TEST_BORROW = true;
    private static final boolean DEFAULT_POOL_TEST_RETURN = true;
    private static final boolean DEFAULT_POOL_LIFO = true;
    private static final boolean ENABLE_CLIENT_BATCH_SEND_REQUEST = CONFIG.getBoolean(ConfigurationKeys.ENABLE_CLIENT_BATCH_SEND_REQUEST, DEFAULT_ENABLE_CLIENT_BATCH_SEND_REQUEST);

    /**
     * Gets connect timeout millis.
     *
     * @return the connect timeout millis
     */
    public int getConnectTimeoutMillis() {
        return connectTimeoutMillis;
    }

    /**
     * Sets connect timeout millis.
     *
     * @param connectTimeoutMillis the connect timeout millis
     */
    public void setConnectTimeoutMillis(int connectTimeoutMillis) {
        this.connectTimeoutMillis = connectTimeoutMillis;
    }

    /**
     * Gets client socket snd buf size.
     *
     * @return the client socket snd buf size
     */
    public int getClientSocketSndBufSize() {
        return clientSocketSndBufSize;
    }

    /**
     * Sets client socket snd buf size.
     *
     * @param clientSocketSndBufSize the client socket snd buf size
     */
    public void setClientSocketSndBufSize(int clientSocketSndBufSize) {
        this.clientSocketSndBufSize = clientSocketSndBufSize;
    }

    /**
     * Gets client socket rcv buf size.
     *
     * @return the client socket rcv buf size
     */
    public int getClientSocketRcvBufSize() {
        return clientSocketRcvBufSize;
    }

    /**
     * Sets client socket rcv buf size.
     *
     * @param clientSocketRcvBufSize the client socket rcv buf size
     */
    public void setClientSocketRcvBufSize(int clientSocketRcvBufSize) {
        this.clientSocketRcvBufSize = clientSocketRcvBufSize;
    }

    /**
     * Gets client channel max idle time seconds.
     *
     * @return the client channel max idle time seconds
     */
    public int getChannelMaxWriteIdleSeconds() {
        return MAX_WRITE_IDLE_SECONDS;
    }

    /**
     * Gets channel max read idle seconds.
     *
     * @return the channel max read idle seconds
     */
    public int getChannelMaxReadIdleSeconds() {
        return MAX_READ_IDLE_SECONDS;
    }

    /**
     * Gets channel max all idle seconds.
     *
     * @return the channel max all idle seconds
     */
    public int getChannelMaxAllIdleSeconds() {
        return MAX_ALL_IDLE_SECONDS;
    }

    /**
     * Gets client worker threads.
     *
     * @return the client worker threads
     */
    public int getClientWorkerThreads() {
        return clientWorkerThreads;
    }

    /**
     * Sets client worker threads.
     *
     * @param clientWorkerThreads the client worker threads
     */
    public void setClientWorkerThreads(int clientWorkerThreads) {
        this.clientWorkerThreads = clientWorkerThreads;
    }

    /**
     * Gets client channel clazz.
     *
     * @return the client channel clazz
     */
    public Class<? extends Channel> getClientChannelClazz() {
        return clientChannelClazz;
    }

    /**
     * Enable native boolean.
     *
     * @return the boolean
     */
    public boolean enableNative() {
        return TRANSPORT_SERVER_TYPE == TransportServerType.NATIVE;
    }

    /**
     * Gets per host max conn.
     *
     * @return the per host max conn
     */
    public int getPerHostMaxConn() {
        return perHostMaxConn;
    }

    /**
     * Sets per host max conn.
     *
     * @param perHostMaxConn the per host max conn
     */
    public void setPerHostMaxConn(int perHostMaxConn) {
        if (perHostMaxConn > PER_HOST_MIN_CONN) {
            this.perHostMaxConn = perHostMaxConn;
        } else {
            this.perHostMaxConn = PER_HOST_MIN_CONN;
        }
    }

    /**
     * Gets pending conn size.
     *
     * @return the pending conn size
     */
    public int getPendingConnSize() {
        return pendingConnSize;
    }

    /**
     * Sets pending conn size.
     *
     * @param pendingConnSize the pending conn size
     */
    public void setPendingConnSize(int pendingConnSize) {
        this.pendingConnSize = pendingConnSize;
    }

    /**
     * Gets rpc RM sendAsyncRequestWithResponse time out.
     *
     * @return the rpc RM sendAsyncRequestWithResponse time out
     */
    public static long getRpcRmRequestTimeout() {
        return RPC_RM_REQUEST_TIMEOUT;
    }

    /**
     * Gets rpc TM sendAsyncRequestWithResponse time out.
     *
     * @return the rpc TM sendAsyncRequestWithResponse time out
     */
    public static long getRpcTmRequestTimeout() {
        return RPC_TM_REQUEST_TIMEOUT;
    }

    /**
     * Gets vgroup.
     *
     * @return the vgroup
     */
    public static String getVgroup() {
        return vgroup;
    }

    /**
     * Sets vgroup.
     *
     * @param vgroup the vgroup
     */
    public static void setVgroup(String vgroup) {
        NettyClientConfig.vgroup = vgroup;
    }

    /**
     * Gets client app name.
     *
     * @return the client app name
     */
    public static String getClientAppName() {
        return clientAppName;
    }

    /**
     * Sets client app name.
     *
     * @param clientAppName the client app name
     */
    public static void setClientAppName(String clientAppName) {
        NettyClientConfig.clientAppName = clientAppName;
    }

    /**
     * Gets client type.
     *
     * @return the client type
     */
    public static int getClientType() {
        return clientType;
    }

    /**
     * Sets client type.
     *
     * @param clientType the client type
     */
    public static void setClientType(int clientType) {
        NettyClientConfig.clientType = clientType;
    }

    /**
     * Gets max inactive channel check.
     *
     * @return the max inactive channel check
     */
    public static int getMaxInactiveChannelCheck() {
        return maxInactiveChannelCheck;
    }

    /**
     * Gets max not writeable retry.
     *
     * @return the max not writeable retry
     */
    public static int getMaxNotWriteableRetry() {
        return MAX_NOT_WRITEABLE_RETRY;
    }

    /**
     * Gets per host min conn.
     *
     * @return the per host min conn
     */
    public static int getPerHostMinConn() {
        return PER_HOST_MIN_CONN;
    }

    /**
     * Gets max check alive retry.
     *
     * @return the max check alive retry
     */
    public static int getMaxCheckAliveRetry() {
        return MAX_CHECK_ALIVE_RETRY;
    }

    /**
     * Gets check alive interval.
     *
     * @return the check alive interval
     */
    public static int getCheckAliveInterval() {
        return CHECK_ALIVE_INTERVAL;
    }

    /**
     * Gets socket address start char.
     *
     * @return the socket address start char
     */
    public static String getSocketAddressStartChar() {
        return SOCKET_ADDRESS_START_CHAR;
    }

    /**
     * Gets client selector thread size.
     *
     * @return the client selector thread size
     */
    public int getClientSelectorThreadSize() {
        return CONFIG.getInt(ConfigurationKeys.CLIENT_SELECTOR_THREAD_SIZE, DEFAULT_SELECTOR_THREAD_SIZE);
    }

    /**
     * Get max acquire conn mills long.
     *
     * @return the long
     */
    public long getMaxAcquireConnMills() {
        return MAX_ACQUIRE_CONN_MILLS;
    }

    /**
     * Get client selector thread prefix string.
     *
     * @return the string
     */
    public String getClientSelectorThreadPrefix() {
        return CONFIG.getConfig(ConfigurationKeys.CLIENT_SELECTOR_THREAD_PREFIX, DEFAULT_SELECTOR_THREAD_PREFIX);
    }

    /**
     * Get client worker thread prefix string.
     *
     * @return the string
     */
    public String getClientWorkerThreadPrefix() {
        return CONFIG.getConfig(ConfigurationKeys.CLIENT_WORKER_THREAD_PREFIX, DEFAULT_WORKER_THREAD_PREFIX);
    }

    /**
     * Get rpc dispatch thread prefix string.
     *
     * @return the string
     */
    public String getRpcDispatchThreadPrefix() {
        return RPC_DISPATCH_THREAD_PREFIX;
    }

    /**
     * Gets max pool active.
     *
     * @return the max pool active
     */
    public int getMaxPoolActive() {
        return DEFAULT_MAX_POOL_ACTIVE;
    }

    /**
     * Gets min pool idle.
     *
     * @return the min pool idle
     */
    public int getMinPoolIdle() {
        return DEFAULT_MIN_POOL_IDLE;
    }

    /**
     * Is pool test borrow boolean.
     *
     * @return the boolean
     */
    public boolean isPoolTestBorrow() {
        return DEFAULT_POOL_TEST_BORROW;
    }

    /**
     * Is pool test return boolean.
     *
     * @return the boolean
     */
    public boolean isPoolTestReturn() {
        return DEFAULT_POOL_TEST_RETURN;
    }

    /**
     * Is pool fifo boolean.
     *
     * @return the boolean
     */
    public boolean isPoolLifo() {
        return DEFAULT_POOL_LIFO;
    }

    /**
     * Gets tm dispatch thread prefix.
     *
     * @return the tm dispatch thread prefix
     */
    public String getTmDispatchThreadPrefix() {
        return RPC_DISPATCH_THREAD_PREFIX + "_" + NettyPoolKey.TransactionRole.TMROLE.name();
    }

    /**
     * Gets rm dispatch thread prefix.
     *
     * @return the rm dispatch thread prefix
     */
    public String getRmDispatchThreadPrefix() {
        return RPC_DISPATCH_THREAD_PREFIX + "_" + NettyPoolKey.TransactionRole.RMROLE.name();
    }

    @Deprecated
    public static boolean isEnableClientBatchSendRequest() {
        return ENABLE_CLIENT_BATCH_SEND_REQUEST;
    }

    /**
     * Get whether enable tls.
     *
     * @return the boolean
     */
    public boolean isEnableTls() {
        return CONFIG.getBoolean(ConfigurationKeys.CLIENT_ENABLE_TLS, DEFAULT_CLIENT_ENABLE_TLS);
    }

    /**
     * Get the client SslContext.
     *
     * @return the SslContext
     */
    public SslContext getSslContext() {
        SslContext sslContext = null;
        String tlsVersion = getTlsVersion();
        String trustCertificatePath = getTrustCertificatePath();
        File trustCertificate = null;
        if (StringUtils.isNotBlank(trustCertificatePath)) {
            trustCertificate = new File(trustCertificatePath);
        }
        try {
            SslContextBuilder sslContextBuilder = SslContextBuilder.forClient()
                .trustManager(trustCertificate).clientAuth(ClientAuth.NONE);
            if (tlsVersion != null) {
                sslContextBuilder.protocols(tlsVersion);
            }
            sslContext = sslContextBuilder.build();
        } catch (SSLException e) {
            LOGGER.error("init client ssl context error:{}", e.getMessage(), e);
        }
        return sslContext;
    }

    /**
     * Get trusted certificate path. The file should contain an X.509
     * certificate in PEM format. {@code null} uses the system default.
     *
     * @return the String
     */
    private String getTrustCertificatePath() {
        return CONFIG.getConfig(ConfigurationKeys.CLIENT_TRUST_CERTIFICATE_PATH);
    }

    /**
     * Get the TLS protocol version to enable
     * or {@code null} to enable the default version.
     *
     * @return the String
     */
    private String getTlsVersion() {
        return CONFIG.getConfig(ConfigurationKeys.CLIENT_TLS_VERSION);
    }
}
