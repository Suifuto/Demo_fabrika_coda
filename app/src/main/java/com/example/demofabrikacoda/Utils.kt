package com.example.demofabrikacoda

import android.util.Log
import io.ipfs.cid.Cid
import io.ipfs.multiaddr.MultiAddress
import io.libp2p.core.PeerId
import io.libp2p.core.Stream
import io.libp2p.core.crypto.PrivKey
import io.netty.handler.codec.http.FullHttpRequest
import io.netty.handler.codec.http.HttpContent
import org.peergos.BlockRequestAuthoriser
import org.peergos.EmbeddedIpfs
import org.peergos.HostBuilder
import org.peergos.Want
import org.peergos.blockstore.RamBlockstore
import org.peergos.config.IdentitySection
import org.peergos.protocol.dht.RamRecordStore
import org.peergos.protocol.http.HttpProtocol
import java.net.InetSocketAddress
import java.net.SocketAddress
import java.util.Optional
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object Utils {
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
        val provideBlocks = true

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

        Log.d("IPFS", "IPFS.node " + ipfs?.node.toString())
        Log.d("IPFS", "IPFS.blockstore " + ipfs?.blockstore.toString())
        Log.d("IPFS", "IPFS.records " + ipfs?.records.toString())
        Log.d("IPFS", "IPFS.dht " + ipfs?.dht.toString())
        Log.d("IPFS", "IPFS.bitswap " + ipfs?.bitswap.toString())
        Log.d("IPFS", "IPFS.p2pHttp " + ipfs?.p2pHttp.toString())
        Log.d("IPFS", "IPFS.blocks " + ipfs?.blocks.toString())

        val wants =
//            listOf<Want?>(Want(Cid.decode("zdpuAwfJrGYtiGFDcSV3rDpaUrqCtQZRxMjdC6Eq9PNqLqTGg")))
            listOf<Want?>(Want(Cid.decode(cid)))
        val retrieveFrom =
            setOf<PeerId?>(PeerId.fromBase58("QmVdFZgHnEgcedCS2G2ZNiEN59LuVrnRm7z3yXtEBv2XiF"))
        val addToLocal = true
        val blocks =
            ipfs.getBlocks(
                wants,
                setOf(), // retrieveFrom,
                addToLocal,
            )
        val data = blocks.get(0)?.block

        return "Received ${data?.size} bytes"
        return "Подключи либу"
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
