package com.example.demofabrikacoda

import android.util.Log
import com.example.demofabrikacoda.data.AppConst
import com.example.demofabrikacoda.data.PingModel
import io.ipfs.cid.Cid
import io.ipfs.multiaddr.MultiAddress
import io.libp2p.core.Host
import io.libp2p.core.PeerId
import io.libp2p.core.Stream
import io.libp2p.core.crypto.PrivKey
import io.libp2p.core.multiformats.Multiaddr
import io.libp2p.core.multistream.ProtocolBinding
import io.libp2p.protocol.Ping
import io.libp2p.protocol.PingController
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpContent
import org.peergos.BlockRequestAuthoriser
import org.peergos.EmbeddedIpfs
import org.peergos.HashedBlock
import org.peergos.HostBuilder
import org.peergos.Want
import org.peergos.blockstore.RamBlockstore
import org.peergos.config.IdentitySection
import org.peergos.protocol.bitswap.Bitswap
import org.peergos.protocol.bitswap.BitswapEngine
import org.peergos.protocol.dht.RamRecordStore
import org.peergos.protocol.http.HttpProtocol
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.time.Instant
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

class IpfsRepository {
    private var _ipfs: EmbeddedIpfs? = null
    val ipfs: EmbeddedIpfs? = _ipfs

    private var _pingNode: Host? = null
    val pingNode: Host? = _pingNode

    private var pinger: PingController? = null

    private val hostname = "localhost"
    private val hostPort = 10000
    private val pingPort = 10005

    fun startIpfs(node: String) {
        val swarmAddresses = listOf<MultiAddress?>(
//            MultiAddress("/ip6/::/tcp/4001")
        )
        val bootstrapAddresses =
            listOf<MultiAddress?>(
                MultiAddress(node),
            )
        val authoriser =
            BlockRequestAuthoriser { cid: Cid?, peerid: Cid?, auth: String? ->
//                Log.d("IPFS", "authoriser $cid, $peerid, $auth")
                CompletableFuture.completedFuture<Boolean?>(true)
            }
        val builder: HostBuilder = HostBuilder().generateIdentity()
        val privKey: PrivKey = builder.privateKey
        val peerId: PeerId? = builder.peerId
        val identity = IdentitySection(privKey.bytes(), peerId)
        val provideBlocks = false

        val httpTarget: SocketAddress = InetSocketAddress(hostname, hostPort)
        val httpProxyTarget: Optional<HttpProtocol.HttpRequestProcessor> =
            Optional.of(
                HttpProtocol.HttpRequestProcessor {
                        s: Stream?,
                        req: FullHttpRequest?,
                        h: Consumer<HttpContent?>?,
                    ->
                    HttpProtocol.proxyRequest(
                        req,
                        httpTarget,
                        h,
                    )
                },
            )

        _ipfs =
            EmbeddedIpfs.build(
                RamRecordStore(),
                RamBlockstore(),
                provideBlocks,
                swarmAddresses,
                bootstrapAddresses,
                identity,
                authoriser,
                httpProxyTarget,
            )
        _ipfs?.start()

//        Log.d("IPFS-info", "IPFS.node peerId " + _ipfs?.node?.peerId.toString())
    }

    fun stopIpfs() {
        _ipfs?.stop()
        _pingNode?.stop()
        _pingNode = null
        pinger = null
    }

    fun getDataFromNode(
        node: String,
        cid: String,
    ): List<HashedBlock> {
        if (_ipfs == null || _ipfs?.node == null) {
            startIpfs(node)
        }

        // want - то что хотим запросить
        val wants = listOf<Want?>(
            Want(Cid.decode(cid))
        )
        val retrieveFrom = setOf<PeerId?>()
        val addToLocal = true
        val blocks =
            _ipfs?.getBlocks(
                wants,
                retrieveFrom,
                addToLocal,
            )

        return blocks ?: listOf()
    }

    fun createPingNode() {
        _pingNode?.stop()
        _pingNode = HostBuilder.build(
            pingPort,
            listOf<ProtocolBinding<*>?>(
                Ping(),
                Bitswap(
                    BitswapEngine(
                        RamBlockstore(),
                        BlockRequestAuthoriser { c: Cid?, p: Cid?, a: String? ->
                            CompletableFuture.completedFuture<Boolean?>(
                                true
                            )
                        },
                        Bitswap.MAX_MESSAGE_SIZE
                    )
                )
            )
        )
        _pingNode?.start()?.join()
        createPinger()
    }

    fun createPinger() {
        _pingNode ?: throw Exception("Ошибка инициализации пинг ноды")
        val address =
            Multiaddr.fromString(AppConst.TEST_NODE)
//            Multiaddr.fromString("/ip4/127.0.0.1/tcp/" + nodePort + "/p2p/" + node.peerId)

//        Log.d("IPFS-ping","Sending ping messages to $address")

        pinger = Ping()
            .dial(_pingNode!!, address)
            .controller
            .join()
    }

    fun runIpfsPing(): Long? {
        _ipfs ?: throw Exception("Ошибка инициализации IPFS")

        if (_pingNode == null || pinger == null) {
            createPingNode()
        }

        val latency = pinger?.ping()?.join()
//            Log.d("IPFS-ping", "startTimeStamp $startTimeStamp latency $latency ms")

        return latency
    }
}