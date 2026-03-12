package com.example.demofabrikacoda

import android.util.Log
import io.ipfs.cid.Cid
import io.ipfs.multiaddr.MultiAddress
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
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object Utils {
    const val TEST_NODE =
        "/dns4/ipfs.infra.cf.team/tcp/4001/p2p/12D3KooWKiqj21VphU2eE25438to5xeny6eP6d3PXT93ZczagPLT"
    const val TEST_CID = "QmTBimFzPPP2QsB7TQGc2dr4BZD4i7Gm2X1mNtb6DqN9Dr"

    fun getDataFromNode(
        node: String,
        cid: String,
    ): String {
        val swarmAddresses = listOf<MultiAddress>()
        // listOf<MultiAddress?>(MultiAddress("/ip6/::/tcp/4001"))
        val bootstrapAddresses =
            listOf<MultiAddress?>(
//                MultiAddress("/dnsaddr/bootstrap.libp2p.io/p2p/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa")
                MultiAddress(node),
            )
        val authoriser =
            BlockRequestAuthoriser { cid: Cid?, peerid: Cid?, auth: String? ->
                Log.d("IPFS", "authoriser $cid, $peerid, $auth")
                CompletableFuture.completedFuture<Boolean?>(true)
            }
        val builder: HostBuilder = HostBuilder().generateIdentity()
        val privKey: PrivKey = builder.privateKey
        val peerId: PeerId? = builder.peerId
        val identity = IdentitySection(privKey.bytes(), peerId)
        val provideBlocks = false

        val httpTarget: SocketAddress = InetSocketAddress("localhost", 10000)
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

        val ipfs =
            EmbeddedIpfs.build(
                RamRecordStore(),
                RamBlockstore(), // FileBlockstore(Path.of("/home/alice/ipfs")),
                provideBlocks,
                listOf(), // swarmAddresses,
                bootstrapAddresses,
                identity,
                authoriser,
                httpProxyTarget,
            )
        ipfs.start()

        ipfs.node.listenAddresses().getOrNull(0)?.let {
            runPing(node = it)
        }

        Log.d("IPFS-info", "IPFS.node peerId " + ipfs?.node?.peerId.toString())
        Log.d("IPFS-info", "IPFS.blockstore " + ipfs?.blockstore?.get(Cid.decode(cid)))

        // want - то что хотим запросить
        val wants =
//            listOf<Want?>(Want(Cid.decode("zdpuAwfJrGYtiGFDcSV3rDpaUrqCtQZRxMjdC6Eq9PNqLqTGg")))
            listOf<Want?>(Want(Cid.decode(cid)))
        // retrieveFrom - вот и что это? Это PeerId Откуда читаем или куда читаем. Какой сюда
        // нужно подставлять или как оно само будет его искать?
//        val retrieveFrom =
//            setOf<PeerId?>(PeerId.fromBase58("QmVdFZgHnEgcedCS2G2ZNiEN59LuVrnRm7z3yXtEBv2XiF"))
        val addToLocal = true
        var data: String
        val blocks =
            ipfs.getBlocks(
                wants,
                setOf<PeerId?>(
//                    PeerId.fromBase58("12D3KooWKiqj21VphU2eE25438to5xeny6eP6d3PXT93ZczagPLT"),
                ),
                addToLocal,
            )
        data = blocks.getOrNull(0)?.block.toString()

        return data
    }

    fun runPing(
        port: Int = 10005,
        node: Multiaddr,
    ) {
        val localHostNode =
            HostBuilder.build(
                port,
                listOf<ProtocolBinding<*>?>(
                    Ping(),
                    Bitswap(
                        BitswapEngine(
                            RamBlockstore(),
                            BlockRequestAuthoriser { c: Cid?, p: Cid?, a: String? ->
                                CompletableFuture.completedFuture<Boolean?>(
                                    true,
                                )
                            },
                            Bitswap.MAX_MESSAGE_SIZE,
                        ),
                    ),
                ),
            )
        localHostNode.start().join()
        try {
            val pinger: PingController =
                Ping()
                    .dial(localHostNode, node)
                    .controller
                    .join()

            Log.d("IPFS-ping", "Sending ping messages to $node")
            for (i in 0..5) {
                val latency = pinger.ping().join()
                Log.d("IPFS-ping", "Ping " + i + ", latency " + latency + "ms")
            }
        } finally {
            localHostNode.stop()
        }
    }

    //
// fun getDataFromNode(node: String, cid: String): String {
//    val swarmAddresses = listOf(MultiAddress("/ip6/::/tcp/4001"))
//    val bootstrapAddresses = listOf(
//        MultiAddress("/dnsaddr/bootstrap.libp2p.io/p2p/QmQCU2EcMqAqQPR2i9bChDtGNJchTbq5TbXJJ16u19uLTa")
//    )
//
//    val authoriser: BlockRequestAuthoriser = { cid, peerId, auth ->
//        java.util.concurrent.CompletableFuture.completedFuture(true)
//    }
//
//    val builder = HostBuilder().generateIdentity()
//    val privKey = builder.privateKey
//    val peerId = builder.peerId
//    val identity = IdentitySection(privKey.bytes(), peerId)
//    val provideBlocks = true
//
//    val httpTarget = InetSocketAddress("localhost", 10000)
//    val httpProxyTarget: Optional<HttpProtocol.HttpRequestProcessor> =
//        Optional.of { s, req, h ->
//            HttpProtocol.proxyRequest(req, httpTarget, h)
//        }
//
//
//    val ipfs = EmbeddedIpfs.build(
//        RamRecordStore(),
//        FileBlockstore(Path.of("/home/alice/ipfs")),
//        provideBlocks,
//        swarmAddresses,
//        bootstrapAddresses,
//        identity,
//        authoriser,
//        httpProxyTarget
//    )
//
//    ipfs.start()
//
//    // Пример получения блоков
//    val wants = listOf(Want(Cid.decode("zdpuAwfJrGYtiGFDcSV3rDpaUrqCtQZRxMjdC6Eq9PNqLqTGg")))
//    val retrieveFrom = setOf(PeerId.fromBase58("QmVdFZgHnEgcedCS2G2ZNiEN59LuVrnRm7z3yXtEBv2XiF"))
//    val addToLocal = true
//    val blocks = ipfs.getBlocks(wants, retrieveFrom, addToLocal)
//    val data = blocks[0].block
//
//    return "Received ${data.size} bytes"
// }
}
