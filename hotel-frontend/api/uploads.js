export default async function handler(req, res) {
  const { path } = req.query;
  const subPath = Array.isArray(path) ? path.join("/") : path || "";
  const targetUrl = `https://nonincorporative-kadence-unlikably.ngrok-free.dev/uploads/${subPath}`;

  try {
    const upstreamRes = await fetch(targetUrl, {
      headers: {
        "ngrok-skip-browser-warning": "true",
      },
    });

    if (!upstreamRes.ok) {
      return res.status(upstreamRes.status).send("Image not found");
    }

    const contentType = upstreamRes.headers.get("content-type") || "image/jpeg";
    res.setHeader("Content-Type", contentType);
    res.setHeader("Cache-Control", "public, max-age=86400, stale-while-revalidate=3600");

    const arrayBuffer = await upstreamRes.arrayBuffer();
    return res.status(200).send(Buffer.from(arrayBuffer));
  } catch (err) {
    return res.status(500).send(err.message || "Failed to proxy image");
  }
}
