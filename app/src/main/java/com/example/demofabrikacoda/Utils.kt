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
import org.peergos.HashedBlock
import org.peergos.HostBuilder
import org.peergos.RamAddressBook
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
    const val TEST_NODE =
        "/dns4/ipfs.infra.cf.team/tcp/4001/p2p/12D3KooWKiqj21VphU2eE25438to5xeny6eP6d3PXT93ZczagPLT"
    const val TEST_CID = "QmTBimFzPPP2QsB7TQGc2dr4BZD4i7Gm2X1mNtb6DqN9Dr"

    private var ipfs: EmbeddedIpfs? = null

    private fun startIpfs(node: String) {
        val swarmAddresses = listOf<MultiAddress?>(
//            MultiAddress("/ip6/::/tcp/4001")
        )
        val bootstrapAddresses =
            listOf<MultiAddress?>(
                MultiAddress(node),
            )
        val authoriser =
            BlockRequestAuthoriser { cid: Cid?, peerid: Cid?, auth: String? ->
                Log.d("IPFS", "authoriser $cid, $peerid, $auth")
                CompletableFuture.completedFuture<Boolean?>(true)
            }
        val builder: HostBuilder = HostBuilder(RamAddressBook()).generateIdentity()
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

        ipfs =
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
        ipfs?.start(false)

        Log.d("IPFS-info", "IPFS.node peerId " + Utils.ipfs?.node?.peerId.toString())
    }

    fun stopIpfs() {
        ipfs?.stop()
    }

    fun getDataFromNode(
        node: String,
        cid: String,
    ): List<HashedBlock> {
        if (ipfs == null || ipfs?.node == null) {
            startIpfs(node)
        }
//        ipfs.node.listenAddresses().getOrNull(0)?.let {
//            runPing(node = it)
//        }

        // want - то что хотим запросить
        val wants = listOf<Want?>(
            Want(Cid.decode(cid))
        )
        val retrieveFrom = setOf<PeerId?>()
        val addToLocal = true
        val blocks =
            ipfs?.getBlocks(
                wants,
                retrieveFrom,
                addToLocal,
            )

        return blocks ?: listOf()
    }

}
