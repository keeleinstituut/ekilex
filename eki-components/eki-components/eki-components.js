var Xi = Object.defineProperty;
var gn = (e) => {
  throw TypeError(e);
};
var es = (e, t, r) => t in e ? Xi(e, t, { enumerable: !0, configurable: !0, writable: !0, value: r }) : e[t] = r;
var T = (e, t, r) => es(e, typeof t != "symbol" ? t + "" : t, r), Nr = (e, t, r) => t.has(e) || gn("Cannot " + r);
var a = (e, t, r) => (Nr(e, t, "read from private field"), r ? r.call(e) : t.get(e)), x = (e, t, r) => t.has(e) ? gn("Cannot add the same private member more than once") : t instanceof WeakSet ? t.add(e) : t.set(e, r), m = (e, t, r, n) => (Nr(e, t, "write to private field"), n ? n.call(e, r) : t.set(e, r), r), $ = (e, t, r) => (Nr(e, t, "access private method"), r);
var Fn;
typeof window < "u" && ((Fn = window.__svelte ?? (window.__svelte = {})).v ?? (Fn.v = /* @__PURE__ */ new Set())).add("5");
const ts = 1, rs = 2, ns = 16, is = 4, ss = 2, Vn = "[", rn = "[!", _n = "[?", nn = "]", Mt = {}, q = Symbol("uninitialized"), Yn = "http://www.w3.org/1999/xhtml", Un = !1;
var Kn = Array.isArray, os = Array.prototype.indexOf, lt = Array.prototype.includes, Er = Array.from, pr = Object.keys, vr = Object.defineProperty, bt = Object.getOwnPropertyDescriptor, ls = Object.getOwnPropertyDescriptors, as = Object.prototype, fs = Array.prototype, Gn = Object.getPrototypeOf, mn = Object.isExtensible;
function us(e) {
  return typeof e == "function";
}
const _t = () => {
};
function cs(e) {
  for (var t = 0; t < e.length; t++)
    e[t]();
}
function Wn() {
  var e, t, r = new Promise((n, i) => {
    e = n, t = i;
  });
  return { promise: r, resolve: e, reject: t };
}
const D = 2, Lt = 4, Tr = 8, Zn = 1 << 24, oe = 16, ke = 32, je = 64, Ir = 128, le = 512, I = 1024, j = 2048, $e = 4096, H = 8192, ae = 16384, Ze = 32768, wn = 1 << 25, dt = 65536, gr = 1 << 17, ds = 1 << 18, vt = 1 << 19, hs = 1 << 20, qe = 1 << 25, ht = 65536, _r = 1 << 21, xt = 1 << 22, Ye = 1 << 23, Or = Symbol("$state"), ps = Symbol("legacy props"), vs = Symbol(""), ar = Symbol("attributes"), jr = Symbol("class"), gs = Symbol("style"), jt = Symbol("text"), Sr = new class extends Error {
  constructor() {
    super(...arguments);
    T(this, "name", "StaleReactionError");
    T(this, "message", "The reaction that called `getAbortSignal()` was re-run or destroyed");
  }
}();
var Hn;
const _s = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  !!((Hn = globalThis.document) != null && Hn.contentType) && /* @__PURE__ */ globalThis.document.contentType.includes("xml")
), sn = 3, er = 8;
function ms() {
  throw new Error("https://svelte.dev/e/async_derived_orphan");
}
function ws(e, t, r) {
  throw new Error("https://svelte.dev/e/each_key_duplicate");
}
function ys() {
  throw new Error("https://svelte.dev/e/effect_update_depth_exceeded");
}
function bs() {
  throw new Error("https://svelte.dev/e/hydration_failed");
}
function xs() {
  throw new Error("https://svelte.dev/e/state_descriptors_fixed");
}
function ks() {
  throw new Error("https://svelte.dev/e/state_prototype_fixed");
}
function $s() {
  throw new Error("https://svelte.dev/e/state_unsafe_mutation");
}
function Es() {
  throw new Error("https://svelte.dev/e/svelte_boundary_reset_onerror");
}
function Ts() {
  console.warn("https://svelte.dev/e/derived_inert");
}
function Cr(e) {
  console.warn("https://svelte.dev/e/hydration_mismatch");
}
function Ss() {
  console.warn("https://svelte.dev/e/svelte_boundary_reset_noop");
}
let M = !1;
function Ie(e) {
  M = e;
}
let L;
function X(e) {
  if (e === null)
    throw Cr(), Mt;
  return L = e;
}
function Ar() {
  return X(/* @__PURE__ */ Pe(L));
}
function we(e) {
  if (M) {
    if (/* @__PURE__ */ Pe(L) !== null)
      throw Cr(), Mt;
    L = e;
  }
}
function Cs(e = 1) {
  if (M) {
    for (var t = e, r = L; t--; )
      r = /** @type {TemplateNode} */
      /* @__PURE__ */ Pe(r);
    L = r;
  }
}
function mr(e = !0) {
  for (var t = 0, r = L; ; ) {
    if (r.nodeType === er) {
      var n = (
        /** @type {Comment} */
        r.data
      );
      if (n === nn) {
        if (t === 0) return r;
        t -= 1;
      } else (n === Vn || n === rn || // "[1", "[2", etc. for if blocks
      n[0] === "[" && !isNaN(Number(n.slice(1)))) && (t += 1);
    }
    var i = (
      /** @type {TemplateNode} */
      /* @__PURE__ */ Pe(r)
    );
    e && r.remove(), r = i;
  }
}
function Qn(e) {
  if (!e || e.nodeType !== er)
    throw Cr(), Mt;
  return (
    /** @type {Comment} */
    e.data
  );
}
function Jn(e) {
  return e === this.v;
}
function As(e, t) {
  return e != e ? t == t : e !== t || e !== null && typeof e == "object" || typeof e == "function";
}
function Xn(e) {
  return !As(e, this.v);
}
let Ms = !1, ge = null;
function Nt(e) {
  ge = e;
}
function on(e, t = !1, r) {
  ge = {
    p: ge,
    i: !1,
    c: null,
    e: null,
    s: e,
    x: null,
    r: (
      /** @type {Effect} */
      k
    ),
    l: null
  };
}
function ln(e) {
  var t = (
    /** @type {ComponentContext} */
    ge
  ), r = t.e;
  if (r !== null) {
    t.e = null;
    for (var n of r)
      Ws(n);
  }
  return e !== void 0 && (t.x = e), t.i = !0, ge = t.p, e ?? /** @type {T} */
  {};
}
function ei() {
  return !0;
}
let Je = [];
function ti() {
  var e = Je;
  Je = [], cs(e);
}
function at(e) {
  if (Je.length === 0 && !Vt) {
    var t = Je;
    queueMicrotask(() => {
      t === Je && ti();
    });
  }
  Je.push(e);
}
function Ls() {
  for (; Je.length > 0; )
    ti();
}
function ri(e) {
  var t = k;
  if (t === null)
    return E.f |= Ye, e;
  if ((t.f & Ze) === 0 && (t.f & Lt) === 0)
    throw e;
  Ve(e, t);
}
function Ve(e, t) {
  for (; t !== null; ) {
    if ((t.f & Ir) !== 0) {
      if ((t.f & Ze) === 0)
        throw e;
      try {
        t.b.error(e);
        return;
      } catch (r) {
        e = r;
      }
    }
    t = t.parent;
  }
  throw e;
}
const Ns = -7169;
function R(e, t) {
  e.f = e.f & Ns | t;
}
function an(e) {
  (e.f & le) !== 0 || e.deps === null ? R(e, I) : R(e, $e);
}
function ni(e) {
  if (e !== null)
    for (const t of e)
      (t.f & D) === 0 || (t.f & ht) === 0 || (t.f ^= ht, ni(
        /** @type {Derived} */
        t.deps
      ));
}
function ii(e, t, r) {
  (e.f & j) !== 0 ? t.add(e) : (e.f & $e) !== 0 && r.add(e), ni(e.deps), R(e, I);
}
let Rr = null, gt = null, b = null, Bt = null, P = null, Pr = null, Vt = !1, zr = !1, wt = null, fr = null;
var yn = 0;
let Os = 1;
var kt, He, tt, $t, Et, rt, Tt, Me, St, V, Gt, Le, de, ye, Ct, nt, A, Dr, Pt, Fr, si, oi, mt, Rs, Dt;
const xr = class xr {
  constructor() {
    x(this, A);
    T(this, "id", Os++);
    /** True as soon as `#process` was called */
    x(this, kt, !1);
    T(this, "linked", !0);
    /** @type {Batch | null} */
    x(this, He, null);
    /** @type {Batch | null} */
    x(this, tt, null);
    /** @type {Map<Effect, ReturnType<typeof deferred<any>>>} */
    T(this, "async_deriveds", /* @__PURE__ */ new Map());
    /**
     * The current values of any signals that are updated in this batch.
     * Tuple format: [value, is_derived] (note: is_derived is false for deriveds, too, if they were overridden via assignment)
     * They keys of this map are identical to `this.#previous`
     * @type {Map<Value, [any, boolean]>}
     */
    T(this, "current", /* @__PURE__ */ new Map());
    /**
     * The values of any signals (sources and deriveds) that are updated in this batch _before_ those updates took place.
     * They keys of this map are identical to `this.#current`
     * @type {Map<Value, any>}
     */
    T(this, "previous", /* @__PURE__ */ new Map());
    /**
     * When the batch is committed (and the DOM is updated), we need to remove old branches
     * and append new ones by calling the functions added inside (if/each/key/etc) blocks
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, $t, /* @__PURE__ */ new Set());
    /**
     * If a fork is discarded, we need to destroy any effects that are no longer needed
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, Et, /* @__PURE__ */ new Set());
    /**
     * Callbacks that should run only when a fork is committed.
     * @type {Set<(batch: Batch) => void>}
     */
    x(this, rt, /* @__PURE__ */ new Set());
    /**
     * The number of async effects that are currently in flight
     */
    x(this, Tt, 0);
    /**
     * Async effects that are currently in flight, _not_ inside a pending boundary
     * @type {Map<Effect, number>}
     */
    x(this, Me, /* @__PURE__ */ new Map());
    /**
     * A deferred that resolves when the batch is committed, used with `settled()`
     * TODO replace with Promise.withResolvers once supported widely enough
     * @type {{ promise: Promise<void>, resolve: (value?: any) => void, reject: (reason: unknown) => void } | null}
     */
    x(this, St, null);
    /**
     * The root effects that need to be flushed
     * @type {Effect[]}
     */
    x(this, V, []);
    /**
     * Effects created while this batch was active.
     * @type {Effect[]}
     */
    x(this, Gt, []);
    /**
     * Deferred effects (which run after async work has completed) that are DIRTY
     * @type {Set<Effect>}
     */
    x(this, Le, /* @__PURE__ */ new Set());
    /**
     * Deferred effects that are MAYBE_DIRTY
     * @type {Set<Effect>}
     */
    x(this, de, /* @__PURE__ */ new Set());
    /**
     * A map of branches that still exist, but will be destroyed when this batch
     * is committed — we skip over these during `process`.
     * The value contains child effects that were dirty/maybe_dirty before being reset,
     * so they can be rescheduled if the branch survives.
     * @type {Map<Effect, { d: Effect[], m: Effect[] }>}
     */
    x(this, ye, /* @__PURE__ */ new Map());
    /**
     * Inverse of #skipped_branches which we need to tell prior batches to unskip them when committing
     * @type {Set<Effect>}
     */
    x(this, Ct, /* @__PURE__ */ new Set());
    T(this, "is_fork", !1);
    x(this, nt, !1);
    gt === null ? Rr = gt = this : (m(gt, tt, this), m(this, He, gt)), gt = this;
  }
  /**
   * Add an effect to the #skipped_branches map and reset its children
   * @param {Effect} effect
   */
  skip_effect(t) {
    a(this, ye).has(t) || a(this, ye).set(t, { d: [], m: [] }), a(this, Ct).delete(t);
  }
  /**
   * Remove an effect from the #skipped_branches map and reschedule
   * any tracked dirty/maybe_dirty child effects
   * @param {Effect} effect
   * @param {(e: Effect) => void} callback
   */
  unskip_effect(t, r = (n) => this.schedule(n)) {
    var n = a(this, ye).get(t);
    if (n) {
      a(this, ye).delete(t);
      for (var i of n.d)
        R(i, j), r(i);
      for (i of n.m)
        R(i, $e), r(i);
    }
    a(this, Ct).add(t);
  }
  /**
   * Associate a change to a given source with the current
   * batch, noting its previous and current values
   * @param {Value} source
   * @param {any} value
   * @param {boolean} [is_derived]
   */
  capture(t, r, n = !1) {
    t.v !== q && !this.previous.has(t) && this.previous.set(t, t.v), (t.f & Ye) === 0 && (this.current.set(t, [r, n]), P == null || P.set(t, r)), this.is_fork || (t.v = r);
  }
  activate() {
    b = this;
  }
  deactivate() {
    b = null, P = null;
  }
  flush() {
    try {
      zr = !0, b = this, $(this, A, Pt).call(this);
    } finally {
      yn = 0, Pr = null, wt = null, fr = null, zr = !1, b = null, P = null, ft.clear();
    }
  }
  discard() {
    var t;
    for (const r of a(this, Et)) r(this);
    a(this, Et).clear(), a(this, rt).clear(), $(this, A, Dt).call(this), (t = a(this, St)) == null || t.resolve();
  }
  /**
   * @param {Effect} effect
   */
  register_created_effect(t) {
    a(this, Gt).push(t);
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  increment(t, r) {
    if (m(this, Tt, a(this, Tt) + 1), t) {
      let n = a(this, Me).get(r) ?? 0;
      a(this, Me).set(r, n + 1);
    }
  }
  /**
   * @param {boolean} blocking
   * @param {Effect} effect
   */
  decrement(t, r) {
    if (m(this, Tt, a(this, Tt) - 1), t) {
      let n = a(this, Me).get(r) ?? 0;
      n === 1 ? a(this, Me).delete(r) : a(this, Me).set(r, n - 1);
    }
    a(this, nt) || (m(this, nt, !0), at(() => {
      m(this, nt, !1), this.linked && this.flush();
    }));
  }
  /**
   * @param {Set<Effect>} dirty_effects
   * @param {Set<Effect>} maybe_dirty_effects
   */
  transfer_effects(t, r) {
    for (const n of t)
      a(this, Le).add(n);
    for (const n of r)
      a(this, de).add(n);
    t.clear(), r.clear();
  }
  /** @param {(batch: Batch) => void} fn */
  oncommit(t) {
    a(this, $t).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  ondiscard(t) {
    a(this, Et).add(t);
  }
  /** @param {(batch: Batch) => void} fn */
  on_fork_commit(t) {
    a(this, rt).add(t);
  }
  run_fork_commit_callbacks() {
    for (const t of a(this, rt)) t(this);
    a(this, rt).clear();
  }
  settled() {
    return (a(this, St) ?? m(this, St, Wn())).promise;
  }
  static ensure() {
    if (b === null) {
      const t = b = new xr();
      !zr && !Vt && at(() => {
        a(t, kt) || t.flush();
      });
    }
    return b;
  }
  apply() {
    {
      P = null;
      return;
    }
  }
  /**
   *
   * @param {Effect} effect
   */
  schedule(t) {
    var i;
    if (Pr = t, (i = t.b) != null && i.is_pending && (t.f & (Lt | Tr | Zn)) !== 0 && (t.f & Ze) === 0) {
      t.b.defer_effect(t);
      return;
    }
    for (var r = t; r.parent !== null; ) {
      r = r.parent;
      var n = r.f;
      if (wt !== null && r === k && (E === null || (E.f & D) === 0))
        return;
      if ((n & (je | ke)) !== 0) {
        if ((n & I) === 0)
          return;
        r.f ^= I;
      }
    }
    a(this, V).push(r);
  }
};
kt = new WeakMap(), He = new WeakMap(), tt = new WeakMap(), $t = new WeakMap(), Et = new WeakMap(), rt = new WeakMap(), Tt = new WeakMap(), Me = new WeakMap(), St = new WeakMap(), V = new WeakMap(), Gt = new WeakMap(), Le = new WeakMap(), de = new WeakMap(), ye = new WeakMap(), Ct = new WeakMap(), nt = new WeakMap(), A = new WeakSet(), Dr = function() {
  if (this.is_fork) return !0;
  for (const n of a(this, Me).keys()) {
    for (var t = n, r = !1; t.parent !== null; ) {
      if (a(this, ye).has(t)) {
        r = !0;
        break;
      }
      t = t.parent;
    }
    if (!r)
      return !0;
  }
  return !1;
}, Pt = function() {
  var f, u, v, h;
  m(this, kt, !0), yn++ > 1e3 && ($(this, A, Dt).call(this), zs());
  for (const c of a(this, Le))
    a(this, de).delete(c), R(c, j), this.schedule(c);
  for (const c of a(this, de))
    R(c, $e), this.schedule(c);
  const t = a(this, V);
  m(this, V, []), this.apply();
  var r = wt = [], n = [], i = fr = [];
  for (const c of t)
    try {
      $(this, A, Fr).call(this, c, r, n);
    } catch (p) {
      throw fi(c), $(this, A, Dr).call(this) || this.discard(), p;
    }
  if (b = null, i.length > 0) {
    var s = xr.ensure();
    for (const c of i)
      s.schedule(c);
  }
  if (wt = null, fr = null, $(this, A, Dr).call(this)) {
    $(this, A, mt).call(this, n), $(this, A, mt).call(this, r);
    for (const [c, p] of a(this, ye))
      ai(c, p);
    i.length > 0 && /** @type {unknown} */
    $(f = b, A, Pt).call(f);
    return;
  }
  const o = $(this, A, si).call(this);
  if (o) {
    $(this, A, mt).call(this, n), $(this, A, mt).call(this, r), $(u = o, A, oi).call(u, this);
    return;
  }
  a(this, Le).clear(), a(this, de).clear();
  for (const c of a(this, $t)) c(this);
  a(this, $t).clear(), Bt = this, bn(n), bn(r), Bt = null, (v = a(this, St)) == null || v.resolve();
  var l = (
    /** @type {Batch | null} */
    /** @type {unknown} */
    b
  );
  if (a(this, Tt) === 0 && (a(this, V).length === 0 || l !== null) && $(this, A, Dt).call(this), a(this, V).length > 0)
    if (l !== null) {
      const c = l;
      a(c, V).push(...a(this, V).filter((p) => !a(c, V).includes(p)));
    } else
      l = this;
  l !== null && $(h = l, A, Pt).call(h);
}, /**
 * Traverse the effect tree, executing effects or stashing
 * them for later execution as appropriate
 * @param {Effect} root
 * @param {Effect[]} effects
 * @param {Effect[]} render_effects
 */
Fr = function(t, r, n) {
  t.f ^= I;
  for (var i = t.first; i !== null; ) {
    var s = i.f, o = (s & (ke | je)) !== 0, l = o && (s & I) !== 0, f = l || (s & H) !== 0 || a(this, ye).has(i);
    if (!f && i.fn !== null) {
      o ? i.f ^= I : (s & Lt) !== 0 ? r.push(i) : tr(i) && ((s & oe) !== 0 && a(this, de).add(i), Rt(i));
      var u = i.first;
      if (u !== null) {
        i = u;
        continue;
      }
    }
    for (; i !== null; ) {
      var v = i.next;
      if (v !== null) {
        i = v;
        break;
      }
      i = i.parent;
    }
  }
}, si = function() {
  for (var t = a(this, He); t !== null; ) {
    if (!t.is_fork) {
      for (const [r, [, n]] of this.current)
        if (t.current.has(r) && !n)
          return t;
    }
    t = a(t, He);
  }
  return null;
}, /**
 * @param {Batch} batch
 */
oi = function(t) {
  var n;
  for (const [i, s] of t.current)
    !this.previous.has(i) && t.previous.has(i) && this.previous.set(i, t.previous.get(i)), this.current.set(i, s);
  for (const [i, s] of t.async_deriveds) {
    const o = this.async_deriveds.get(i);
    o && s.promise.then(o.resolve).catch(o.reject);
  }
  this.transfer_effects(a(t, Le), a(t, de));
  const r = (i) => {
    var s = i.reactions;
    if (s !== null)
      for (const f of s) {
        var o = f.f;
        if ((o & D) !== 0)
          r(
            /** @type {Derived} */
            f
          );
        else {
          var l = (
            /** @type {Effect} */
            f
          );
          o & (xt | oe) && !this.async_deriveds.has(l) && (a(this, de).delete(l), R(l, j), this.schedule(l));
        }
      }
  };
  for (const i of this.current.keys())
    r(i);
  this.oncommit(() => t.discard()), $(n = t, A, Dt).call(n), b = this, $(this, A, Pt).call(this);
}, /**
 * @param {Effect[]} effects
 */
mt = function(t) {
  for (var r = 0; r < t.length; r += 1)
    ii(t[r], a(this, Le), a(this, de));
}, Rs = function() {
  var v;
  for (let h = Rr; h !== null; h = a(h, tt)) {
    var t = h.id < this.id, r = [];
    for (const [c, [p, d]] of this.current) {
      if (h.current.has(c)) {
        var n = (
          /** @type {[any, boolean]} */
          h.current.get(c)[0]
        );
        if (t && p !== n)
          h.current.set(c, [p, d]);
        else
          continue;
      }
      r.push(c);
    }
    if (t)
      for (const [c, p] of this.async_deriveds) {
        const d = h.async_deriveds.get(c);
        d && p.promise.then(d.resolve).catch(d.reject);
      }
    if (a(h, kt)) {
      var i = [...h.current.keys()].filter(
        (c) => !/** @type {[any, boolean]} */
        h.current.get(c)[1] && !this.current.has(c)
      );
      if (i.length === 0)
        t && h.discard();
      else if (r.length > 0) {
        if (t)
          for (const c of a(this, Ct))
            h.unskip_effect(c, (p) => {
              var d;
              (p.f & (oe | xt)) !== 0 ? h.schedule(p) : $(d = h, A, mt).call(d, [p]);
            });
        h.activate();
        var s = /* @__PURE__ */ new Set(), o = /* @__PURE__ */ new Map();
        for (var l of r)
          li(l, i, s, o);
        o = /* @__PURE__ */ new Map();
        var f = [...h.current].filter(([c, p]) => {
          const d = this.current.get(c);
          return d ? d[0] !== p[0] || d[1] !== p[1] : !0;
        }).map(([c]) => c);
        if (f.length > 0)
          for (const c of a(this, Gt))
            (c.f & (ae | H | gr)) === 0 && fn(c, f, o) && ((c.f & (xt | oe)) !== 0 ? (R(c, j), h.schedule(c)) : a(h, Le).add(c));
        if (a(h, V).length > 0 && !a(h, nt)) {
          h.apply();
          for (var u of a(h, V))
            $(v = h, A, Fr).call(v, u, [], []);
          m(h, V, []);
        }
        h.deactivate();
      }
    }
  }
}, Dt = function() {
  if (this.linked) {
    var t = a(this, He), r = a(this, tt);
    t === null ? Rr = r : m(t, tt, r), r === null ? gt = t : m(r, He, t), this.linked = !1;
  }
};
let Ue = xr;
function Hr(e) {
  var t = Vt;
  Vt = !0;
  try {
    for (var r; ; ) {
      if (Ls(), b === null)
        return (
          /** @type {T} */
          r
        );
      b.flush();
    }
  } finally {
    Vt = t;
  }
}
function zs() {
  try {
    ys();
  } catch (e) {
    Ve(e, Pr);
  }
}
let ce = null;
function bn(e) {
  var t = e.length;
  if (t !== 0) {
    for (var r = 0; r < t; ) {
      var n = e[r++];
      if ((n.f & (ae | H)) === 0 && tr(n) && (ce = /* @__PURE__ */ new Set(), Rt(n), n.deps === null && n.first === null && n.nodes === null && n.teardown === null && n.ac === null && Ci(n), (ce == null ? void 0 : ce.size) > 0)) {
        ft.clear();
        for (const i of ce) {
          if ((i.f & (ae | H)) !== 0) continue;
          const s = [i];
          let o = i.parent;
          for (; o !== null; )
            ce.has(o) && (ce.delete(o), s.push(o)), o = o.parent;
          for (let l = s.length - 1; l >= 0; l--) {
            const f = s[l];
            (f.f & (ae | H)) === 0 && Rt(f);
          }
        }
        ce.clear();
      }
    }
    ce = null;
  }
}
function li(e, t, r, n) {
  if (!r.has(e) && (r.add(e), e.reactions !== null))
    for (const i of e.reactions) {
      const s = i.f;
      (s & D) !== 0 ? li(
        /** @type {Derived} */
        i,
        t,
        r,
        n
      ) : (s & (xt | oe)) !== 0 && (s & j) === 0 && fn(i, t, n) && (R(i, j), un(
        /** @type {Effect} */
        i
      ));
    }
}
function fn(e, t, r) {
  const n = r.get(e);
  if (n !== void 0) return n;
  if (e.deps !== null)
    for (const i of e.deps) {
      if (lt.call(t, i))
        return !0;
      if ((i.f & D) !== 0 && fn(
        /** @type {Derived} */
        i,
        t,
        r
      ))
        return r.set(
          /** @type {Derived} */
          i,
          !0
        ), !0;
    }
  return r.set(e, !1), !1;
}
function un(e) {
  b.schedule(e);
}
function ai(e, t) {
  if (!((e.f & ke) !== 0 && (e.f & I) !== 0)) {
    (e.f & j) !== 0 ? t.d.push(e) : (e.f & $e) !== 0 && t.m.push(e), R(e, I);
    for (var r = e.first; r !== null; )
      ai(r, t), r = r.next;
  }
}
function fi(e) {
  R(e, I);
  for (var t = e.first; t !== null; )
    fi(t), t = t.next;
}
function qs(e) {
  let t = 0, r = pt(0), n;
  return () => {
    dn() && (z(r), Ti(() => (t === 0 && (n = ji(() => e(() => Yt(r)))), t += 1, () => {
      at(() => {
        t -= 1, t === 0 && (n == null || n(), n = void 0, Yt(r));
      });
    })));
  };
}
var Is = dt | vt;
function js(e, t, r, n) {
  new Ps(e, t, r, n);
}
var Z, Wt, re, it, Y, ne, F, Q, Ne, st, Be, At, Zt, Qt, Oe, kr, N, ui, ci, di, Br, ur, cr, Vr, Yr;
class Ps {
  /**
   * @param {TemplateNode} node
   * @param {BoundaryProps} props
   * @param {((anchor: Node) => void)} children
   * @param {((error: unknown) => unknown) | undefined} [transform_error]
   */
  constructor(t, r, n, i) {
    x(this, N);
    /** @type {Boundary | null} */
    T(this, "parent");
    T(this, "is_pending", !1);
    /**
     * API-level transformError transform function. Transforms errors before they reach the `failed` snippet.
     * Inherited from parent boundary, or defaults to identity.
     * @type {(error: unknown) => unknown}
     */
    T(this, "transform_error");
    /** @type {TemplateNode} */
    x(this, Z);
    /** @type {TemplateNode | null} */
    x(this, Wt, M ? L : null);
    /** @type {BoundaryProps} */
    x(this, re);
    /** @type {((anchor: Node) => void)} */
    x(this, it);
    /** @type {Effect} */
    x(this, Y);
    /** @type {Effect | null} */
    x(this, ne, null);
    /** @type {Effect | null} */
    x(this, F, null);
    /** @type {Effect | null} */
    x(this, Q, null);
    /** @type {DocumentFragment | null} */
    x(this, Ne, null);
    x(this, st, 0);
    x(this, Be, 0);
    x(this, At, !1);
    /** @type {Set<Effect>} */
    x(this, Zt, /* @__PURE__ */ new Set());
    /** @type {Set<Effect>} */
    x(this, Qt, /* @__PURE__ */ new Set());
    /**
     * A source containing the number of pending async deriveds/expressions.
     * Only created if `$effect.pending()` is used inside the boundary,
     * otherwise updating the source results in needless `Batch.ensure()`
     * calls followed by no-op flushes
     * @type {Source<number> | null}
     */
    x(this, Oe, null);
    x(this, kr, qs(() => (m(this, Oe, pt(a(this, st))), () => {
      m(this, Oe, null);
    })));
    var s;
    m(this, Z, t), m(this, re, r), m(this, it, (o) => {
      var l = (
        /** @type {Effect} */
        k
      );
      l.b = this, l.f |= Ir, n(o);
    }), this.parent = /** @type {Effect} */
    k.b, this.transform_error = i ?? ((s = this.parent) == null ? void 0 : s.transform_error) ?? ((o) => o), m(this, Y, hn(() => {
      if (M) {
        const o = (
          /** @type {Comment} */
          a(this, Wt)
        );
        Ar();
        const l = o.data === rn;
        if (o.data.startsWith(_n)) {
          const u = JSON.parse(o.data.slice(_n.length));
          $(this, N, ci).call(this, u);
        } else l ? $(this, N, di).call(this) : $(this, N, ui).call(this);
      } else
        $(this, N, Br).call(this);
    }, Is)), M && m(this, Z, L);
  }
  /**
   * Defer an effect inside a pending boundary until the boundary resolves
   * @param {Effect} effect
   */
  defer_effect(t) {
    ii(t, a(this, Zt), a(this, Qt));
  }
  /**
   * Returns `false` if the effect exists inside a boundary whose pending snippet is shown
   * @returns {boolean}
   */
  is_rendered() {
    return !this.is_pending && (!this.parent || this.parent.is_rendered());
  }
  has_pending_snippet() {
    return !!a(this, re).pending;
  }
  /**
   * Update the source that powers `$effect.pending()` inside this boundary,
   * and controls when the current `pending` snippet (if any) is removed.
   * Do not call from inside the class
   * @param {1 | -1} d
   * @param {Batch} batch
   */
  update_pending_count(t, r) {
    $(this, N, Vr).call(this, t, r), m(this, st, a(this, st) + t), !(!a(this, Oe) || a(this, At)) && (m(this, At, !0), at(() => {
      m(this, At, !1), a(this, Oe) && Ot(a(this, Oe), a(this, st));
    }));
  }
  get_effect_pending() {
    return a(this, kr).call(this), z(
      /** @type {Source<number>} */
      a(this, Oe)
    );
  }
  /** @param {unknown} error */
  error(t) {
    if (!a(this, re).onerror && !a(this, re).failed)
      throw t;
    b != null && b.is_fork ? (a(this, ne) && b.skip_effect(a(this, ne)), a(this, F) && b.skip_effect(a(this, F)), a(this, Q) && b.skip_effect(a(this, Q)), b.on_fork_commit(() => {
      $(this, N, Yr).call(this, t);
    })) : $(this, N, Yr).call(this, t);
  }
}
Z = new WeakMap(), Wt = new WeakMap(), re = new WeakMap(), it = new WeakMap(), Y = new WeakMap(), ne = new WeakMap(), F = new WeakMap(), Q = new WeakMap(), Ne = new WeakMap(), st = new WeakMap(), Be = new WeakMap(), At = new WeakMap(), Zt = new WeakMap(), Qt = new WeakMap(), Oe = new WeakMap(), kr = new WeakMap(), N = new WeakSet(), ui = function() {
  try {
    m(this, ne, se(() => a(this, it).call(this, a(this, Z))));
  } catch (t) {
    this.error(t);
  }
}, /**
 * @param {unknown} error The deserialized error from the server's hydration comment
 */
ci = function(t) {
  const r = a(this, re).failed;
  r && m(this, Q, se(() => {
    r(
      a(this, Z),
      () => t,
      () => () => {
      }
    );
  }));
}, di = function() {
  const t = a(this, re).pending;
  t && (this.is_pending = !0, m(this, F, se(() => t(a(this, Z)))), at(() => {
    var r = m(this, Ne, document.createDocumentFragment()), n = xe();
    r.append(n), m(this, ne, $(this, N, cr).call(this, () => se(() => a(this, it).call(this, n)))), a(this, Be) === 0 && (a(this, Z).before(r), m(this, Ne, null), ut(
      /** @type {Effect} */
      a(this, F),
      () => {
        m(this, F, null);
      }
    ), $(this, N, ur).call(
      this,
      /** @type {Batch} */
      b
    ));
  }));
}, Br = function() {
  try {
    if (this.is_pending = this.has_pending_snippet(), m(this, Be, 0), m(this, st, 0), m(this, ne, se(() => {
      a(this, it).call(this, a(this, Z));
    })), a(this, Be) > 0) {
      var t = m(this, Ne, document.createDocumentFragment());
      vn(a(this, ne), t);
      const r = (
        /** @type {(anchor: Node) => void} */
        a(this, re).pending
      );
      m(this, F, se(() => r(a(this, Z))));
    } else
      $(this, N, ur).call(
        this,
        /** @type {Batch} */
        b
      );
  } catch (r) {
    this.error(r);
  }
}, /**
 * @param {Batch} batch
 */
ur = function(t) {
  this.is_pending = !1, t.transfer_effects(a(this, Zt), a(this, Qt));
}, /**
 * @template T
 * @param {() => T} fn
 */
cr = function(t) {
  var r = k, n = E, i = ge;
  Ee(a(this, Y)), ue(a(this, Y)), Nt(a(this, Y).ctx);
  try {
    return Ue.ensure(), t();
  } catch (s) {
    return ri(s), null;
  } finally {
    Ee(r), ue(n), Nt(i);
  }
}, /**
 * Updates the pending count associated with the currently visible pending snippet,
 * if any, such that we can replace the snippet with content once work is done
 * @param {1 | -1} d
 * @param {Batch} batch
 */
Vr = function(t, r) {
  var n;
  if (!this.has_pending_snippet()) {
    this.parent && $(n = this.parent, N, Vr).call(n, t, r);
    return;
  }
  m(this, Be, a(this, Be) + t), a(this, Be) === 0 && ($(this, N, ur).call(this, r), a(this, F) && ut(a(this, F), () => {
    m(this, F, null);
  }), a(this, Ne) && (a(this, Z).before(a(this, Ne)), m(this, Ne, null)));
}, /**
 * @param {unknown} error
 */
Yr = function(t) {
  a(this, ne) && (B(a(this, ne)), m(this, ne, null)), a(this, F) && (B(a(this, F)), m(this, F, null)), a(this, Q) && (B(a(this, Q)), m(this, Q, null)), M && (X(
    /** @type {TemplateNode} */
    a(this, Wt)
  ), Cs(), X(mr()));
  var r = a(this, re).onerror;
  let n = a(this, re).failed;
  var i = !1, s = !1;
  const o = () => {
    if (i) {
      Ss();
      return;
    }
    i = !0, s && Es(), a(this, Q) !== null && ut(a(this, Q), () => {
      m(this, Q, null);
    }), $(this, N, cr).call(this, () => {
      $(this, N, Br).call(this);
    });
  }, l = (f) => {
    try {
      s = !0, r == null || r(f, o), s = !1;
    } catch (u) {
      Ve(u, a(this, Y) && a(this, Y).parent);
    }
    n && m(this, Q, $(this, N, cr).call(this, () => {
      try {
        return se(() => {
          var u = (
            /** @type {Effect} */
            k
          );
          u.b = this, u.f |= Ir, n(
            a(this, Z),
            () => f,
            () => o
          );
        });
      } catch (u) {
        return Ve(
          u,
          /** @type {Effect} */
          a(this, Y).parent
        ), null;
      }
    }));
  };
  at(() => {
    var f;
    try {
      f = this.transform_error(t);
    } catch (u) {
      Ve(u, a(this, Y) && a(this, Y).parent);
      return;
    }
    f !== null && typeof f == "object" && typeof /** @type {any} */
    f.then == "function" ? f.then(
      l,
      /** @param {unknown} e */
      (u) => Ve(u, a(this, Y) && a(this, Y).parent)
    ) : l(f);
  });
};
function Ds(e, t, r, n) {
  const i = Mr;
  var s = e.filter((c) => !c.settled);
  if (r.length === 0 && s.length === 0) {
    n(t.map(i));
    return;
  }
  var o = (
    /** @type {Effect} */
    k
  ), l = Fs(), f = s.length === 1 ? s[0].promise : s.length > 1 ? Promise.all(s.map((c) => c.promise)) : null;
  function u(c) {
    if ((o.f & ae) === 0) {
      l();
      try {
        n(c);
      } catch (p) {
        Ve(p, o);
      }
      wr();
    }
  }
  var v = hi();
  if (r.length === 0) {
    f.then(() => u(t.map(i))).finally(v);
    return;
  }
  function h() {
    Promise.all(r.map((c) => /* @__PURE__ */ Hs(c))).then((c) => u([...t.map(i), ...c])).catch((c) => Ve(c, o)).finally(v);
  }
  f ? f.then(() => {
    l(), h(), wr();
  }) : h();
}
function Fs() {
  var e = (
    /** @type {Effect} */
    k
  ), t = E, r = ge, n = (
    /** @type {Batch} */
    b
  );
  return function(s = !0) {
    Ee(e), ue(t), Nt(r), s && (e.f & ae) === 0 && (n == null || n.activate(), n == null || n.apply());
  };
}
function wr(e = !0) {
  Ee(null), ue(null), Nt(null), e && (b == null || b.deactivate());
}
function hi() {
  var e = (
    /** @type {Effect} */
    k
  ), t = e.b, r = (
    /** @type {Batch} */
    b
  ), n = !!(t != null && t.is_rendered());
  return t == null || t.update_pending_count(1, r), r.increment(n, e), () => {
    t == null || t.update_pending_count(-1, r), r.decrement(n, e);
  };
}
// @__NO_SIDE_EFFECTS__
function Mr(e) {
  var t = D | j;
  return k !== null && (k.f |= vt), {
    ctx: ge,
    deps: null,
    effects: null,
    equals: Jn,
    f: t,
    fn: e,
    reactions: null,
    rv: 0,
    v: (
      /** @type {V} */
      q
    ),
    wv: 0,
    parent: k,
    ac: null
  };
}
const rr = Symbol("obsolete");
// @__NO_SIDE_EFFECTS__
function Hs(e, t, r) {
  let n = (
    /** @type {Effect | null} */
    k
  );
  n === null && ms();
  var i = (
    /** @type {Promise<V>} */
    /** @type {unknown} */
    void 0
  ), s = pt(
    /** @type {V} */
    q
  ), o = !E, l = /* @__PURE__ */ new Set();
  return Xs(() => {
    var p, d;
    var f = (
      /** @type {Effect} */
      k
    ), u = Wn();
    i = u.promise;
    try {
      Promise.resolve(e()).then(u.resolve, (g) => {
        g !== Sr && u.reject(g);
      }).finally(wr);
    } catch (g) {
      u.reject(g), wr();
    }
    var v = (
      /** @type {Batch} */
      b
    );
    if (o) {
      if ((f.f & Ze) !== 0)
        var h = hi();
      if (
        // boundary can be null if the async derived is inside an $effect.root not connected to the component render tree
        (p = n.b) != null && p.is_rendered()
      )
        (d = v.async_deriveds.get(f)) == null || d.reject(rr);
      else
        for (const g of l.values())
          g.reject(rr);
      l.add(u), v.async_deriveds.set(f, u);
    }
    const c = (g, _ = void 0) => {
      h == null || h(), l.delete(u), _ !== rr && (v.activate(), _ ? (s.f |= Ye, Ot(s, _)) : ((s.f & Ye) !== 0 && (s.f ^= Ye), Ot(s, g)), v.deactivate());
    };
    u.promise.then(c, (g) => c(null, g || "unknown"));
  }), Gs(() => {
    for (const f of l)
      f.reject(rr);
  }), new Promise((f) => {
    function u(v) {
      function h() {
        v === i ? f(s) : u(i);
      }
      v.then(h, h);
    }
    u(i);
  });
}
// @__NO_SIDE_EFFECTS__
function xn(e) {
  const t = /* @__PURE__ */ Mr(e);
  return Li(t), t;
}
// @__NO_SIDE_EFFECTS__
function Bs(e) {
  const t = /* @__PURE__ */ Mr(e);
  return t.equals = Xn, t;
}
function Vs(e) {
  var t = e.effects;
  if (t !== null) {
    e.effects = null;
    for (var r = 0; r < t.length; r += 1)
      B(
        /** @type {Effect} */
        t[r]
      );
  }
}
function cn(e) {
  var t, r = k, n = e.parent;
  if (!Ke && n !== null && e.v !== q && // if it was never evaluated before, it's guaranteed to fail downstream, so we try to execute instead
  (n.f & (ae | H)) !== 0)
    return Ts(), e.v;
  Ee(n);
  try {
    e.f &= ~ht, Vs(e), t = zi(e);
  } finally {
    Ee(r);
  }
  return t;
}
function pi(e) {
  var t = cn(e);
  if (!e.equals(t) && (e.wv = Oi(), (!(b != null && b.is_fork) || e.deps === null) && (b !== null ? (b.capture(e, t, !0), Bt == null || Bt.capture(e, t, !0)) : e.v = t, e.deps === null))) {
    R(e, I);
    return;
  }
  Ke || (P !== null ? (dn() || b != null && b.is_fork) && P.set(e, t) : an(e));
}
function Ys(e) {
  var t, r;
  if (e.effects !== null)
    for (const n of e.effects)
      (n.teardown || n.ac) && ((t = n.teardown) == null || t.call(n), (r = n.ac) == null || r.abort(Sr), n.fn !== null && (n.teardown = _t), n.ac = null, Kt(n, 0), pn(n));
}
function vi(e) {
  if (e.effects !== null)
    for (const t of e.effects)
      t.teardown && t.fn !== null && Rt(t);
}
let yr = /* @__PURE__ */ new Set();
const ft = /* @__PURE__ */ new Map();
let gi = !1;
function pt(e, t) {
  var r = {
    f: 0,
    // TODO ideally we could skip this altogether, but it causes type errors
    v: e,
    reactions: null,
    equals: Jn,
    rv: 0,
    wv: 0
  };
  return r;
}
// @__NO_SIDE_EFFECTS__
function Ae(e, t) {
  const r = pt(e);
  return Li(r), r;
}
// @__NO_SIDE_EFFECTS__
function _i(e, t = !1, r = !0) {
  const n = pt(e);
  return t || (n.equals = Xn), n;
}
function pe(e, t, r = !1) {
  E !== null && // since we are untracking the function inside `$inspect.with` we need to add this check
  // to ensure we error if state is set inside an inspect effect
  (!ve || (E.f & gr) !== 0) && ei() && (E.f & (D | oe | xt | gr)) !== 0 && (fe === null || !lt.call(fe, e)) && $s();
  let n = r ? yt(t) : t;
  return Ot(e, n, fr);
}
function Ot(e, t, r = null) {
  if (!e.equals(t)) {
    ft.set(e, Ke ? t : e.v);
    var n = Ue.ensure();
    if (n.capture(e, t), (e.f & D) !== 0) {
      const i = (
        /** @type {Derived} */
        e
      );
      (e.f & j) !== 0 && cn(i), P === null && an(i);
    }
    e.wv = Oi(), mi(e, j, r), k !== null && (k.f & I) !== 0 && (k.f & (ke | je)) === 0 && (te === null ? ro([e]) : te.push(e)), !n.is_fork && yr.size > 0 && !gi && Us();
  }
  return t;
}
function Us() {
  gi = !1;
  for (const e of yr) {
    (e.f & I) !== 0 && R(e, $e);
    let t;
    try {
      t = tr(e);
    } catch {
      t = !0;
    }
    t && Rt(e);
  }
  yr.clear();
}
function Yt(e) {
  pe(e, e.v + 1);
}
function mi(e, t, r) {
  var n = e.reactions;
  if (n !== null)
    for (var i = n.length, s = 0; s < i; s++) {
      var o = n[s], l = o.f, f = (l & j) === 0;
      if (f && R(o, t), (l & gr) !== 0)
        yr.add(
          /** @type {Effect} */
          o
        );
      else if ((l & D) !== 0) {
        var u = (
          /** @type {Derived} */
          o
        );
        P == null || P.delete(u), (l & ht) === 0 && (l & le && (k === null || (k.f & _r) === 0) && (o.f |= ht), mi(u, $e, r));
      } else if (f) {
        var v = (
          /** @type {Effect} */
          o
        );
        (l & oe) !== 0 && ce !== null && ce.add(v), r !== null ? r.push(v) : un(v);
      }
    }
}
function yt(e) {
  if (typeof e != "object" || e === null || Or in e)
    return e;
  const t = Gn(e);
  if (t !== as && t !== fs)
    return e;
  var r = /* @__PURE__ */ new Map(), n = Kn(e), i = /* @__PURE__ */ Ae(0), s = ct, o = (l) => {
    if (ct === s)
      return l();
    var f = E, u = ct;
    ue(null), En(s);
    var v = l();
    return ue(f), En(u), v;
  };
  return n && r.set("length", /* @__PURE__ */ Ae(
    /** @type {any[]} */
    e.length
  )), new Proxy(
    /** @type {any} */
    e,
    {
      defineProperty(l, f, u) {
        (!("value" in u) || u.configurable === !1 || u.enumerable === !1 || u.writable === !1) && xs();
        var v = r.get(f);
        return v === void 0 ? o(() => {
          var h = /* @__PURE__ */ Ae(u.value);
          return r.set(f, h), h;
        }) : pe(v, u.value, !0), !0;
      },
      deleteProperty(l, f) {
        var u = r.get(f);
        if (u === void 0) {
          if (f in l) {
            const v = o(() => /* @__PURE__ */ Ae(q));
            r.set(f, v), Yt(i);
          }
        } else
          pe(u, q), Yt(i);
        return !0;
      },
      get(l, f, u) {
        var p;
        if (f === Or)
          return e;
        var v = r.get(f), h = f in l;
        if (v === void 0 && (!h || (p = bt(l, f)) != null && p.writable) && (v = o(() => {
          var d = yt(h ? l[f] : q), g = /* @__PURE__ */ Ae(d);
          return g;
        }), r.set(f, v)), v !== void 0) {
          var c = z(v);
          return c === q ? void 0 : c;
        }
        return Reflect.get(l, f, u);
      },
      getOwnPropertyDescriptor(l, f) {
        var u = Reflect.getOwnPropertyDescriptor(l, f);
        if (u && "value" in u) {
          var v = r.get(f);
          v && (u.value = z(v));
        } else if (u === void 0) {
          var h = r.get(f), c = h == null ? void 0 : h.v;
          if (h !== void 0 && c !== q)
            return {
              enumerable: !0,
              configurable: !0,
              value: c,
              writable: !0
            };
        }
        return u;
      },
      has(l, f) {
        var c;
        if (f === Or)
          return !0;
        var u = r.get(f), v = u !== void 0 && u.v !== q || Reflect.has(l, f);
        if (u !== void 0 || k !== null && (!v || (c = bt(l, f)) != null && c.writable)) {
          u === void 0 && (u = o(() => {
            var p = v ? yt(l[f]) : q, d = /* @__PURE__ */ Ae(p);
            return d;
          }), r.set(f, u));
          var h = z(u);
          if (h === q)
            return !1;
        }
        return v;
      },
      set(l, f, u, v) {
        var S;
        var h = r.get(f), c = f in l;
        if (n && f === "length")
          for (var p = u; p < /** @type {Source<number>} */
          h.v; p += 1) {
            var d = r.get(p + "");
            d !== void 0 ? pe(d, q) : p in l && (d = o(() => /* @__PURE__ */ Ae(q)), r.set(p + "", d));
          }
        if (h === void 0)
          (!c || (S = bt(l, f)) != null && S.writable) && (h = o(() => /* @__PURE__ */ Ae(void 0)), pe(h, yt(u)), r.set(f, h));
        else {
          c = h.v !== q;
          var g = o(() => yt(u));
          pe(h, g);
        }
        var _ = Reflect.getOwnPropertyDescriptor(l, f);
        if (_ != null && _.set && _.set.call(v, u), !c) {
          if (n && typeof f == "string") {
            var w = (
              /** @type {Source<number>} */
              r.get("length")
            ), y = Number(f);
            Number.isInteger(y) && y >= w.v && pe(w, y + 1);
          }
          Yt(i);
        }
        return !0;
      },
      ownKeys(l) {
        z(i);
        var f = Reflect.ownKeys(l).filter((h) => {
          var c = r.get(h);
          return c === void 0 || c.v !== q;
        });
        for (var [u, v] of r)
          v.v !== q && !(u in l) && f.push(u);
        return f;
      },
      setPrototypeOf() {
        ks();
      }
    }
  );
}
var kn, wi, yi, bi;
function Ur() {
  if (kn === void 0) {
    kn = window, wi = /Firefox/.test(navigator.userAgent);
    var e = Element.prototype, t = Node.prototype, r = Text.prototype;
    yi = bt(t, "firstChild").get, bi = bt(t, "nextSibling").get, mn(e) && (e[jr] = void 0, e[ar] = null, e[gs] = void 0, e.__e = void 0), mn(r) && (r[jt] = void 0);
  }
}
function xe(e = "") {
  return document.createTextNode(e);
}
// @__NO_SIDE_EFFECTS__
function Ut(e) {
  return (
    /** @type {TemplateNode | null} */
    yi.call(e)
  );
}
// @__NO_SIDE_EFFECTS__
function Pe(e) {
  return (
    /** @type {TemplateNode | null} */
    bi.call(e)
  );
}
function Fe(e, t) {
  if (!M)
    return /* @__PURE__ */ Ut(e);
  var r = /* @__PURE__ */ Ut(L);
  if (r === null)
    r = L.appendChild(xe());
  else if (t && r.nodeType !== sn) {
    var n = xe();
    return r == null || r.before(n), X(n), n;
  }
  return t && Ei(
    /** @type {Text} */
    r
  ), X(r), r;
}
function Ft(e, t = 1, r = !1) {
  let n = M ? L : e;
  for (var i; t--; )
    i = n, n = /** @type {TemplateNode} */
    /* @__PURE__ */ Pe(n);
  if (!M)
    return n;
  if (r) {
    if ((n == null ? void 0 : n.nodeType) !== sn) {
      var s = xe();
      return n === null ? i == null || i.after(s) : n.before(s), X(s), s;
    }
    Ei(
      /** @type {Text} */
      n
    );
  }
  return X(n), n;
}
function xi(e) {
  e.textContent = "";
}
function ki() {
  return !1;
}
function $i(e, t, r) {
  return (
    /** @type {T extends keyof HTMLElementTagNameMap ? HTMLElementTagNameMap[T] : Element} */
    document.createElementNS(Yn, e, void 0)
  );
}
function Ei(e) {
  if (
    /** @type {string} */
    e.nodeValue.length < 65536
  )
    return;
  let t = e.nextSibling;
  for (; t !== null && t.nodeType === sn; )
    t.remove(), e.nodeValue += /** @type {string} */
    t.nodeValue, t = e.nextSibling;
}
function Lr(e) {
  var t = E, r = k;
  ue(null), Ee(null);
  try {
    return e();
  } finally {
    ue(t), Ee(r);
  }
}
function Ks(e, t) {
  var r = t.last;
  r === null ? t.last = t.first = e : (r.next = e, e.prev = r, t.last = e);
}
function Te(e, t) {
  var r = k;
  r !== null && (r.f & H) !== 0 && (e |= H);
  var n = {
    ctx: ge,
    deps: null,
    nodes: null,
    f: e | j | le,
    first: null,
    fn: t,
    last: null,
    next: null,
    parent: r,
    b: r && r.b,
    prev: null,
    teardown: null,
    wv: 0,
    ac: null
  };
  b == null || b.register_created_effect(n);
  var i = n;
  if ((e & Lt) !== 0)
    wt !== null ? wt.push(n) : Ue.ensure().schedule(n);
  else if (t !== null) {
    try {
      Rt(n);
    } catch (o) {
      throw B(n), o;
    }
    i.deps === null && i.teardown === null && i.nodes === null && i.first === i.last && // either `null`, or a singular child
    (i.f & vt) === 0 && (i = i.first, (e & oe) !== 0 && (e & dt) !== 0 && i !== null && (i.f |= dt));
  }
  if (i !== null && (i.parent = r, r !== null && Ks(i, r), E !== null && (E.f & D) !== 0 && (e & je) === 0)) {
    var s = (
      /** @type {Derived} */
      E
    );
    (s.effects ?? (s.effects = [])).push(i);
  }
  return n;
}
function dn() {
  return E !== null && !ve;
}
function Gs(e) {
  const t = Te(Tr, null);
  return R(t, I), t.teardown = e, t;
}
function Ws(e) {
  return Te(Lt | hs, e);
}
function Zs(e) {
  Ue.ensure();
  const t = Te(je | vt, e);
  return () => {
    B(t);
  };
}
function Qs(e) {
  Ue.ensure();
  const t = Te(je | vt, e);
  return (r = {}) => new Promise((n) => {
    r.outro ? ut(t, () => {
      B(t), n(void 0);
    }) : (B(t), n(void 0));
  });
}
function Js(e) {
  return Te(Lt, e);
}
function Xs(e) {
  return Te(xt | vt, e);
}
function Ti(e, t = 0) {
  return Te(Tr | t, e);
}
function qt(e, t = [], r = [], n = []) {
  Ds(n, t, r, (i) => {
    Te(Tr, () => e(...i.map(z)));
  });
}
function hn(e, t = 0) {
  var r = Te(oe | t, e);
  return r;
}
function se(e) {
  return Te(ke | vt, e);
}
function Si(e) {
  var t = e.teardown;
  if (t !== null) {
    const r = Ke, n = E;
    $n(!0), ue(null);
    try {
      t.call(null);
    } finally {
      $n(r), ue(n);
    }
  }
}
function pn(e, t = !1) {
  var r = e.first;
  for (e.first = e.last = null; r !== null; ) {
    const i = r.ac;
    i !== null && Lr(() => {
      i.abort(Sr);
    });
    var n = r.next;
    (r.f & je) !== 0 ? r.parent = null : B(r, t), r = n;
  }
}
function eo(e) {
  for (var t = e.first; t !== null; ) {
    var r = t.next;
    (t.f & ke) === 0 && B(t), t = r;
  }
}
function B(e, t = !0) {
  var r = !1;
  (t || (e.f & ds) !== 0) && e.nodes !== null && e.nodes.end !== null && (to(
    e.nodes.start,
    /** @type {TemplateNode} */
    e.nodes.end
  ), r = !0), R(e, wn), pn(e, t && !r), Kt(e, 0);
  var n = e.nodes && e.nodes.t;
  if (n !== null)
    for (const s of n)
      s.stop();
  Si(e), e.f ^= wn, e.f |= ae;
  var i = e.parent;
  i !== null && i.first !== null && Ci(e), e.next = e.prev = e.teardown = e.ctx = e.deps = e.fn = e.nodes = e.ac = e.b = null;
}
function to(e, t) {
  for (; e !== null; ) {
    var r = e === t ? null : /* @__PURE__ */ Pe(e);
    e.remove(), e = r;
  }
}
function Ci(e) {
  var t = e.parent, r = e.prev, n = e.next;
  r !== null && (r.next = n), n !== null && (n.prev = r), t !== null && (t.first === e && (t.first = n), t.last === e && (t.last = r));
}
function ut(e, t, r = !0) {
  var n = [];
  Ai(e, n, !0);
  var i = () => {
    r && B(e), t && t();
  }, s = n.length;
  if (s > 0) {
    var o = () => --s || i();
    for (var l of n)
      l.out(o);
  } else
    i();
}
function Ai(e, t, r) {
  if ((e.f & H) === 0) {
    e.f ^= H;
    var n = e.nodes && e.nodes.t;
    if (n !== null)
      for (const l of n)
        (l.is_global || r) && t.push(l);
    for (var i = e.first; i !== null; ) {
      var s = i.next;
      if ((i.f & je) === 0) {
        var o = (i.f & dt) !== 0 || // If this is a branch effect without a block effect parent,
        // it means the parent block effect was pruned. In that case,
        // transparency information was transferred to the branch effect.
        (i.f & ke) !== 0 && (e.f & oe) !== 0;
        Ai(i, t, o ? r : !1);
      }
      i = s;
    }
  }
}
function br(e) {
  Mi(e, !0);
}
function Mi(e, t) {
  if ((e.f & H) !== 0) {
    e.f ^= H, (e.f & I) === 0 && (R(e, j), Ue.ensure().schedule(e));
    for (var r = e.first; r !== null; ) {
      var n = r.next, i = (r.f & dt) !== 0 || (r.f & ke) !== 0;
      Mi(r, i ? t : !1), r = n;
    }
    var s = e.nodes && e.nodes.t;
    if (s !== null)
      for (const o of s)
        (o.is_global || t) && o.in();
  }
}
function vn(e, t) {
  if (e.nodes)
    for (var r = e.nodes.start, n = e.nodes.end; r !== null; ) {
      var i = r === n ? null : /* @__PURE__ */ Pe(r);
      t.append(r), r = i;
    }
}
let dr = !1, Ke = !1;
function $n(e) {
  Ke = e;
}
let E = null, ve = !1;
function ue(e) {
  E = e;
}
let k = null;
function Ee(e) {
  k = e;
}
let fe = null;
function Li(e) {
  E !== null && (fe === null ? fe = [e] : fe.push(e));
}
let U = null, W = 0, te = null;
function ro(e) {
  te = e;
}
let Ni = 1, Xe = 0, ct = Xe;
function En(e) {
  ct = e;
}
function Oi() {
  return ++Ni;
}
function tr(e) {
  var t = e.f;
  if ((t & j) !== 0)
    return !0;
  if (t & D && (e.f &= ~ht), (t & $e) !== 0) {
    for (var r = (
      /** @type {Value[]} */
      e.deps
    ), n = r.length, i = 0; i < n; i++) {
      var s = r[i];
      if (tr(
        /** @type {Derived} */
        s
      ) && pi(
        /** @type {Derived} */
        s
      ), s.wv > e.wv)
        return !0;
    }
    (t & le) !== 0 && // During time traveling we don't want to reset the status so that
    // traversal of the graph in the other batches still happens
    P === null && R(e, I);
  }
  return !1;
}
function Ri(e, t, r = !0) {
  var n = e.reactions;
  if (n !== null && !(fe !== null && lt.call(fe, e)))
    for (var i = 0; i < n.length; i++) {
      var s = n[i];
      (s.f & D) !== 0 ? Ri(
        /** @type {Derived} */
        s,
        t,
        !1
      ) : t === s && (r ? R(s, j) : (s.f & I) !== 0 && R(s, $e), un(
        /** @type {Effect} */
        s
      ));
    }
}
function zi(e) {
  var g;
  var t = U, r = W, n = te, i = E, s = fe, o = ge, l = ve, f = ct, u = e.f;
  U = /** @type {null | Value[]} */
  null, W = 0, te = null, E = (u & (ke | je)) === 0 ? e : null, fe = null, Nt(e.ctx), ve = !1, ct = ++Xe, e.ac !== null && (Lr(() => {
    e.ac.abort(Sr);
  }), e.ac = null);
  try {
    e.f |= _r;
    var v = (
      /** @type {Function} */
      e.fn
    ), h = v();
    e.f |= Ze;
    var c = e.deps, p = b == null ? void 0 : b.is_fork;
    if (U !== null) {
      var d;
      if (p || Kt(e, W), c !== null && W > 0)
        for (c.length = W + U.length, d = 0; d < U.length; d++)
          c[W + d] = U[d];
      else
        e.deps = c = U;
      if (dn() && (e.f & le) !== 0)
        for (d = W; d < c.length; d++)
          ((g = c[d]).reactions ?? (g.reactions = [])).push(e);
    } else !p && c !== null && W < c.length && (Kt(e, W), c.length = W);
    if (ei() && te !== null && !ve && c !== null && (e.f & (D | $e | j)) === 0)
      for (d = 0; d < /** @type {Source[]} */
      te.length; d++)
        Ri(
          te[d],
          /** @type {Effect} */
          e
        );
    if (i !== null && i !== e) {
      if (Xe++, i.deps !== null)
        for (let _ = 0; _ < r; _ += 1)
          i.deps[_].rv = Xe;
      if (t !== null)
        for (const _ of t)
          _.rv = Xe;
      te !== null && (n === null ? n = te : n.push(.../** @type {Source[]} */
      te));
    }
    return (e.f & Ye) !== 0 && (e.f ^= Ye), h;
  } catch (_) {
    return ri(_);
  } finally {
    e.f ^= _r, U = t, W = r, te = n, E = i, fe = s, Nt(o), ve = l, ct = f;
  }
}
function no(e, t) {
  let r = t.reactions;
  if (r !== null) {
    var n = os.call(r, e);
    if (n !== -1) {
      var i = r.length - 1;
      i === 0 ? r = t.reactions = null : (r[n] = r[i], r.pop());
    }
  }
  if (r === null && (t.f & D) !== 0 && // Destroying a child effect while updating a parent effect can cause a dependency to appear
  // to be unused, when in fact it is used by the currently-updating parent. Checking `new_deps`
  // allows us to skip the expensive work of disconnecting and immediately reconnecting it
  (U === null || !lt.call(U, t))) {
    var s = (
      /** @type {Derived} */
      t
    );
    (s.f & le) !== 0 && (s.f ^= le, s.f &= ~ht), s.v !== q && an(s), Ys(s), Kt(s, 0);
  }
}
function Kt(e, t) {
  var r = e.deps;
  if (r !== null)
    for (var n = t; n < r.length; n++)
      no(e, r[n]);
}
function Rt(e) {
  var t = e.f;
  if ((t & ae) === 0) {
    R(e, I);
    var r = k, n = dr;
    k = e, dr = !0;
    try {
      (t & (oe | Zn)) !== 0 ? eo(e) : pn(e), Si(e);
      var i = zi(e);
      e.teardown = typeof i == "function" ? i : null, e.wv = Ni;
      var s;
      Un && Ms && (e.f & j) !== 0 && e.deps;
    } finally {
      dr = n, k = r;
    }
  }
}
function z(e) {
  var t = e.f, r = (t & D) !== 0;
  if (E !== null && !ve) {
    var n = k !== null && (k.f & ae) !== 0;
    if (!n && (fe === null || !lt.call(fe, e))) {
      var i = E.deps;
      if ((E.f & _r) !== 0)
        e.rv < Xe && (e.rv = Xe, U === null && i !== null && i[W] === e ? W++ : U === null ? U = [e] : U.push(e));
      else {
        E.deps ?? (E.deps = []), lt.call(E.deps, e) || E.deps.push(e);
        var s = e.reactions;
        s === null ? e.reactions = [E] : lt.call(s, E) || s.push(E);
      }
    }
  }
  if (Ke && ft.has(e))
    return ft.get(e);
  if (r) {
    var o = (
      /** @type {Derived} */
      e
    );
    if (Ke) {
      var l = o.v;
      return ((o.f & I) === 0 && o.reactions !== null || Ii(o)) && (l = cn(o)), ft.set(o, l), l;
    }
    var f = (o.f & le) === 0 && !ve && E !== null && (dr || (E.f & le) !== 0), u = (o.f & Ze) === 0;
    tr(o) && (f && (o.f |= le), pi(o)), f && !u && (vi(o), qi(o));
  }
  if (P != null && P.has(e))
    return P.get(e);
  if ((e.f & Ye) !== 0)
    throw e.v;
  return e.v;
}
function qi(e) {
  if (e.f |= le, e.deps !== null)
    for (const t of e.deps)
      (t.reactions ?? (t.reactions = [])).push(e), (t.f & D) !== 0 && (t.f & le) === 0 && (vi(
        /** @type {Derived} */
        t
      ), qi(
        /** @type {Derived} */
        t
      ));
}
function Ii(e) {
  if (e.v === q) return !0;
  if (e.deps === null) return !1;
  for (const t of e.deps)
    if (ft.has(t) || (t.f & D) !== 0 && Ii(
      /** @type {Derived} */
      t
    ))
      return !0;
  return !1;
}
function ji(e) {
  var t = ve;
  try {
    return ve = !0, e();
  } finally {
    ve = t;
  }
}
const et = Symbol("events"), Pi = /* @__PURE__ */ new Set(), Kr = /* @__PURE__ */ new Set();
function io(e, t, r) {
  (t[et] ?? (t[et] = {}))[e] = r;
}
function so(e) {
  for (var t = 0; t < e.length; t++)
    Pi.add(e[t]);
  for (var r of Kr)
    r(e);
}
let Tn = null;
function Sn(e) {
  var g, _;
  var t = this, r = (
    /** @type {Node} */
    t.ownerDocument
  ), n = e.type, i = ((g = e.composedPath) == null ? void 0 : g.call(e)) || [], s = (
    /** @type {null | Element} */
    i[0] || e.target
  );
  Tn = e;
  var o = 0, l = Tn === e && e[et];
  if (l) {
    var f = i.indexOf(l);
    if (f !== -1 && (t === document || t === /** @type {any} */
    window)) {
      e[et] = t;
      return;
    }
    var u = i.indexOf(t);
    if (u === -1)
      return;
    f <= u && (o = f);
  }
  if (s = /** @type {Element} */
  i[o] || e.target, s !== t) {
    vr(e, "currentTarget", {
      configurable: !0,
      get() {
        return s || r;
      }
    });
    var v = E, h = k;
    ue(null), Ee(null);
    try {
      for (var c, p = []; s !== null && s !== t; ) {
        try {
          var d = (_ = s[et]) == null ? void 0 : _[n];
          d != null && (!/** @type {any} */
          s.disabled || // DOM could've been updated already by the time this is reached, so we check this as well
          // -> the target could not have been disabled because it emits the event in the first place
          e.target === s) && d.call(s, e);
        } catch (w) {
          c ? p.push(w) : c = w;
        }
        if (e.cancelBubble) break;
        o++, s = o < i.length ? (
          /** @type {Element} */
          i[o]
        ) : null;
      }
      if (c) {
        for (let w of p)
          queueMicrotask(() => {
            throw w;
          });
        throw c;
      }
    } finally {
      e[et] = t, delete e.currentTarget, ue(v), Ee(h);
    }
  }
}
var Bn;
const qr = (
  // We gotta write it like this because after downleveling the pure comment may end up in the wrong location
  ((Bn = globalThis == null ? void 0 : globalThis.window) == null ? void 0 : Bn.trustedTypes) && /* @__PURE__ */ globalThis.window.trustedTypes.createPolicy("svelte-trusted-html", {
    /** @param {string} html */
    createHTML: (e) => e
  })
);
function oo(e) {
  return (
    /** @type {string} */
    (qr == null ? void 0 : qr.createHTML(e)) ?? e
  );
}
function lo(e) {
  var t = $i("template");
  return t.innerHTML = oo(e.replaceAll("<!>", "<!---->")), t.content;
}
function Gr(e, t) {
  var r = (
    /** @type {Effect} */
    k
  );
  r.nodes === null && (r.nodes = { start: e, end: t, a: null, t: null });
}
// @__NO_SIDE_EFFECTS__
function zt(e, t) {
  var r = (t & ss) !== 0, n, i = !e.startsWith("<!>");
  return () => {
    if (M)
      return Gr(L, null), L;
    n === void 0 && (n = lo(i ? e : "<!>" + e), n = /** @type {TemplateNode} */
    /* @__PURE__ */ Ut(n));
    var s = (
      /** @type {TemplateNode} */
      r || wi ? document.importNode(n, !0) : n.cloneNode(!0)
    );
    return Gr(s, s), s;
  };
}
function Qe(e, t) {
  if (M) {
    var r = (
      /** @type {Effect & { nodes: EffectNodes }} */
      k
    );
    ((r.f & Ze) === 0 || r.nodes.end === null) && (r.nodes.end = L), Ar();
    return;
  }
  e !== null && e.before(
    /** @type {Node} */
    t
  );
}
const ao = ["touchstart", "touchmove"];
function fo(e) {
  return ao.includes(e);
}
let Wr = !0;
function nr(e, t) {
  var r = t == null ? "" : typeof t == "object" ? `${t}` : t;
  r !== /** @type {any} */
  (e[jt] ?? (e[jt] = e.nodeValue)) && (e[jt] = r, e.nodeValue = `${r}`);
}
function Di(e, t) {
  return Fi(e, t);
}
function uo(e, t) {
  Ur(), t.intro = t.intro ?? !1;
  const r = t.target, n = M, i = L;
  try {
    for (var s = /* @__PURE__ */ Ut(r); s && (s.nodeType !== er || /** @type {Comment} */
    s.data !== Vn); )
      s = /* @__PURE__ */ Pe(s);
    if (!s)
      throw Mt;
    Ie(!0), X(
      /** @type {Comment} */
      s
    );
    const o = Fi(e, { ...t, anchor: s });
    return Ie(!1), /**  @type {Exports} */
    o;
  } catch (o) {
    if (o instanceof Error && o.message.split(`
`).some((l) => l.startsWith("https://svelte.dev/e/")))
      throw o;
    return o !== Mt && console.warn("Failed to hydrate: ", o), t.recover === !1 && bs(), Ur(), xi(r), Ie(!1), Di(e, t);
  } finally {
    Ie(n), X(i);
  }
}
const ir = /* @__PURE__ */ new Map();
function Fi(e, { target: t, anchor: r, props: n = {}, events: i, context: s, intro: o = !0, transformError: l }) {
  Ur();
  var f = void 0, u = Qs(() => {
    var v = r ?? t.appendChild(xe());
    js(
      /** @type {TemplateNode} */
      v,
      {
        pending: () => {
        }
      },
      (p) => {
        on({});
        var d = (
          /** @type {ComponentContext} */
          ge
        );
        if (s && (d.c = s), i && (n.$$events = i), M && Gr(
          /** @type {TemplateNode} */
          p,
          null
        ), Wr = o, f = e(p, n) || {}, Wr = !0, M && (k.nodes.end = L, L === null || L.nodeType !== er || /** @type {Comment} */
        L.data !== nn))
          throw Cr(), Mt;
        ln();
      },
      l
    );
    var h = /* @__PURE__ */ new Set(), c = (p) => {
      for (var d = 0; d < p.length; d++) {
        var g = p[d];
        if (!h.has(g)) {
          h.add(g);
          var _ = fo(g);
          for (const S of [t, document]) {
            var w = ir.get(S);
            w === void 0 && (w = /* @__PURE__ */ new Map(), ir.set(S, w));
            var y = w.get(g);
            y === void 0 ? (S.addEventListener(g, Sn, { passive: _ }), w.set(g, 1)) : w.set(g, y + 1);
          }
        }
      }
    };
    return c(Er(Pi)), Kr.add(c), () => {
      var _;
      for (var p of h)
        for (const w of [t, document]) {
          var d = (
            /** @type {Map<string, number>} */
            ir.get(w)
          ), g = (
            /** @type {number} */
            d.get(p)
          );
          --g == 0 ? (w.removeEventListener(p, Sn), d.delete(p), d.size === 0 && ir.delete(w)) : d.set(p, g);
        }
      Kr.delete(c), v !== r && ((_ = v.parentNode) == null || _.removeChild(v));
    };
  });
  return Zr.set(f, u), f;
}
let Zr = /* @__PURE__ */ new WeakMap();
function co(e, t) {
  const r = Zr.get(e);
  return r ? (Zr.delete(e), r(t)) : Promise.resolve();
}
var he, be, J, ot, Jt, Xt, $r;
class ho {
  /**
   * @param {TemplateNode} anchor
   * @param {boolean} transition
   */
  constructor(t, r = !0) {
    /** @type {TemplateNode} */
    T(this, "anchor");
    /** @type {Map<Batch, Key>} */
    x(this, he, /* @__PURE__ */ new Map());
    /**
     * Map of keys to effects that are currently rendered in the DOM.
     * These effects are visible and actively part of the document tree.
     * Example:
     * ```
     * {#if condition}
     * 	foo
     * {:else}
     * 	bar
     * {/if}
     * ```
     * Can result in the entries `true->Effect` and `false->Effect`
     * @type {Map<Key, Effect>}
     */
    x(this, be, /* @__PURE__ */ new Map());
    /**
     * Similar to #onscreen with respect to the keys, but contains branches that are not yet
     * in the DOM, because their insertion is deferred.
     * @type {Map<Key, Branch>}
     */
    x(this, J, /* @__PURE__ */ new Map());
    /**
     * Keys of effects that are currently outroing
     * @type {Set<Key>}
     */
    x(this, ot, /* @__PURE__ */ new Set());
    /**
     * Whether to pause (i.e. outro) on change, or destroy immediately.
     * This is necessary for `<svelte:element>`
     */
    x(this, Jt, !0);
    /**
     * @param {Batch} batch
     */
    x(this, Xt, (t) => {
      if (a(this, he).has(t)) {
        var r = (
          /** @type {Key} */
          a(this, he).get(t)
        ), n = a(this, be).get(r);
        if (n)
          br(n), a(this, ot).delete(r);
        else {
          var i = a(this, J).get(r);
          i && (br(i.effect), a(this, be).set(r, i.effect), a(this, J).delete(r), i.fragment.lastChild.remove(), this.anchor.before(i.fragment), n = i.effect);
        }
        for (const [s, o] of a(this, he)) {
          if (a(this, he).delete(s), s === t)
            break;
          const l = a(this, J).get(o);
          l && (B(l.effect), a(this, J).delete(o));
        }
        for (const [s, o] of a(this, be)) {
          if (s === r || a(this, ot).has(s)) continue;
          const l = () => {
            if (Array.from(a(this, he).values()).includes(s)) {
              var u = document.createDocumentFragment();
              vn(o, u), u.append(xe()), a(this, J).set(s, { effect: o, fragment: u });
            } else
              B(o);
            a(this, ot).delete(s), a(this, be).delete(s);
          };
          a(this, Jt) || !n ? (a(this, ot).add(s), ut(o, l, !1)) : l();
        }
      }
    });
    /**
     * @param {Batch} batch
     */
    x(this, $r, (t) => {
      a(this, he).delete(t);
      const r = Array.from(a(this, he).values());
      for (const [n, i] of a(this, J))
        r.includes(n) || (B(i.effect), a(this, J).delete(n));
    });
    this.anchor = t, m(this, Jt, r);
  }
  /**
   *
   * @param {any} key
   * @param {null | ((target: TemplateNode) => void)} fn
   */
  ensure(t, r) {
    var n = (
      /** @type {Batch} */
      b
    ), i = ki();
    if (r && !a(this, be).has(t) && !a(this, J).has(t))
      if (i) {
        var s = document.createDocumentFragment(), o = xe();
        s.append(o), a(this, J).set(t, {
          effect: se(() => r(o)),
          fragment: s
        });
      } else
        a(this, be).set(
          t,
          se(() => r(this.anchor))
        );
    if (a(this, he).set(n, t), i) {
      for (const [l, f] of a(this, be))
        l === t ? n.unskip_effect(f) : n.skip_effect(f);
      for (const [l, f] of a(this, J))
        l === t ? n.unskip_effect(f.effect) : n.skip_effect(f.effect);
      n.oncommit(a(this, Xt)), n.ondiscard(a(this, $r));
    } else
      M && (this.anchor = L), a(this, Xt).call(this, n);
  }
}
he = new WeakMap(), be = new WeakMap(), J = new WeakMap(), ot = new WeakMap(), Jt = new WeakMap(), Xt = new WeakMap(), $r = new WeakMap();
function sr(e, t, r = !1) {
  var n;
  M && (n = L, Ar());
  var i = new ho(e), s = r ? dt : 0;
  function o(l, f) {
    if (M) {
      var u = Qn(
        /** @type {TemplateNode} */
        n
      );
      if (l !== parseInt(u.substring(1))) {
        var v = mr();
        X(v), i.anchor = v, Ie(!1), i.ensure(l, f), Ie(!0);
        return;
      }
    }
    i.ensure(l, f);
  }
  hn(() => {
    var l = !1;
    t((f, u = 0) => {
      l = !0, o(u, f);
    }), l || o(-1, null);
  }, s);
}
function po(e, t, r) {
  for (var n = [], i = t.length, s, o = t.length, l = 0; l < i; l++) {
    let h = t[l];
    ut(
      h,
      () => {
        if (s) {
          if (s.pending.delete(h), s.done.add(h), s.pending.size === 0) {
            var c = (
              /** @type {Set<EachOutroGroup>} */
              e.outrogroups
            );
            Qr(e, Er(s.done)), c.delete(s), c.size === 0 && (e.outrogroups = null);
          }
        } else
          o -= 1;
      },
      !1
    );
  }
  if (o === 0) {
    var f = n.length === 0 && r !== null;
    if (f) {
      var u = (
        /** @type {Element} */
        r
      ), v = (
        /** @type {Element} */
        u.parentNode
      );
      xi(v), v.append(u), e.items.clear();
    }
    Qr(e, t, !f);
  } else
    s = {
      pending: new Set(t),
      done: /* @__PURE__ */ new Set()
    }, (e.outrogroups ?? (e.outrogroups = /* @__PURE__ */ new Set())).add(s);
}
function Qr(e, t, r = !0) {
  var n;
  if (e.pending.size > 0) {
    n = /* @__PURE__ */ new Set();
    for (const o of e.pending.values())
      for (const l of o)
        n.add(
          /** @type {EachItem} */
          e.items.get(l).e
        );
  }
  for (var i = 0; i < t.length; i++) {
    var s = t[i];
    if (n != null && n.has(s)) {
      s.f |= qe;
      const o = document.createDocumentFragment();
      vn(s, o);
    } else
      B(t[i], r);
  }
}
var Cn;
function An(e, t, r, n, i, s = null) {
  var o = e, l = /* @__PURE__ */ new Map();
  {
    var f = (
      /** @type {Element} */
      e
    );
    o = M ? X(/* @__PURE__ */ Ut(f)) : f.appendChild(xe());
  }
  M && Ar();
  var u = null, v = /* @__PURE__ */ Bs(() => {
    var y = r();
    return Kn(y) ? y : y == null ? [] : Er(y);
  }), h, c = /* @__PURE__ */ new Map(), p = !0;
  function d(y) {
    (w.effect.f & ae) === 0 && (w.pending.delete(y), w.fallback = u, vo(w, h, o, t, n), u !== null && (h.length === 0 ? (u.f & qe) === 0 ? br(u) : (u.f ^= qe, Ht(u, null, o)) : ut(u, () => {
      u = null;
    })));
  }
  function g(y) {
    w.pending.delete(y);
  }
  var _ = hn(() => {
    h = /** @type {V[]} */
    z(v);
    var y = h.length;
    let S = !1;
    if (M) {
      var O = Qn(o) === rn;
      O !== (y === 0) && (o = mr(), X(o), Ie(!1), S = !0);
    }
    for (var C = /* @__PURE__ */ new Set(), K = (
      /** @type {Batch} */
      b
    ), _e = ki(), ee = 0; ee < y; ee += 1) {
      M && L.nodeType === er && /** @type {Comment} */
      L.data === nn && (o = /** @type {Comment} */
      L, S = !0, Ie(!1));
      var G = h[ee], Se = n(G, ee), Ce = p ? null : l.get(Se);
      Ce ? (Ce.v && Ot(Ce.v, G), Ce.i && Ot(Ce.i, ee), _e && K.unskip_effect(Ce.e)) : (Ce = go(
        l,
        p ? o : Cn ?? (Cn = xe()),
        G,
        Se,
        ee,
        i,
        t,
        r
      ), p || (Ce.e.f |= qe), l.set(Se, Ce)), C.add(Se);
    }
    if (y === 0 && s && !u && (p ? u = se(() => s(o)) : (u = se(() => s(Cn ?? (Cn = xe()))), u.f |= qe)), y > C.size && ws(), M && y > 0 && X(mr()), !p)
      if (c.set(K, C), _e) {
        for (const [Qi, Ji] of l)
          C.has(Qi) || K.skip_effect(Ji.e);
        K.oncommit(d), K.ondiscard(g);
      } else
        d(K);
    S && Ie(!0), z(v);
  }), w = { effect: _, items: l, pending: c, outrogroups: null, fallback: u };
  p = !1, M && (o = L);
}
function It(e) {
  for (; e !== null && (e.f & ke) === 0; )
    e = e.next;
  return e;
}
function vo(e, t, r, n, i) {
  var ee;
  var s = t.length, o = e.items, l = It(e.effect.first), f, u = null, v = [], h = [], c, p, d, g;
  for (g = 0; g < s; g += 1) {
    if (c = t[g], p = i(c, g), d = /** @type {EachItem} */
    o.get(p).e, e.outrogroups !== null)
      for (const G of e.outrogroups)
        G.pending.delete(d), G.done.delete(d);
    if ((d.f & H) !== 0 && br(d), (d.f & qe) !== 0)
      if (d.f ^= qe, d === l)
        Ht(d, null, r);
      else {
        var _ = u ? u.next : l;
        d === e.effect.last && (e.effect.last = d.prev), d.prev && (d.prev.next = d.next), d.next && (d.next.prev = d.prev), De(e, u, d), De(e, d, _), Ht(d, _, r), u = d, v = [], h = [], l = It(u.next);
        continue;
      }
    if (d !== l) {
      if (f !== void 0 && f.has(d)) {
        if (v.length < h.length) {
          var w = h[0], y;
          u = w.prev;
          var S = v[0], O = v[v.length - 1];
          for (y = 0; y < v.length; y += 1)
            Ht(v[y], w, r);
          for (y = 0; y < h.length; y += 1)
            f.delete(h[y]);
          De(e, S.prev, O.next), De(e, u, S), De(e, O, w), l = w, u = O, g -= 1, v = [], h = [];
        } else
          f.delete(d), Ht(d, l, r), De(e, d.prev, d.next), De(e, d, u === null ? e.effect.first : u.next), De(e, u, d), u = d;
        continue;
      }
      for (v = [], h = []; l !== null && l !== d; )
        (f ?? (f = /* @__PURE__ */ new Set())).add(l), h.push(l), l = It(l.next);
      if (l === null)
        continue;
    }
    (d.f & qe) === 0 && v.push(d), u = d, l = It(d.next);
  }
  if (e.outrogroups !== null) {
    for (const G of e.outrogroups)
      G.pending.size === 0 && (Qr(e, Er(G.done)), (ee = e.outrogroups) == null || ee.delete(G));
    e.outrogroups.size === 0 && (e.outrogroups = null);
  }
  if (l !== null || f !== void 0) {
    var C = [];
    if (f !== void 0)
      for (d of f)
        (d.f & H) === 0 && C.push(d);
    for (; l !== null; )
      (l.f & H) === 0 && l !== e.fallback && C.push(l), l = It(l.next);
    var K = C.length;
    if (K > 0) {
      var _e = s === 0 ? r : null;
      po(e, C, _e);
    }
  }
}
function go(e, t, r, n, i, s, o, l) {
  var f = (o & ts) !== 0 ? (o & ns) === 0 ? /* @__PURE__ */ _i(r, !1, !1) : pt(r) : null, u = (o & rs) !== 0 ? pt(i) : null;
  return {
    v: f,
    i: u,
    e: se(() => (s(t, f ?? r, u ?? i, l), () => {
      e.delete(n);
    }))
  };
}
function Ht(e, t, r) {
  if (e.nodes)
    for (var n = e.nodes.start, i = e.nodes.end, s = t && (t.f & qe) === 0 ? (
      /** @type {EffectNodes} */
      t.nodes.start
    ) : r; n !== null; ) {
      var o = (
        /** @type {TemplateNode} */
        /* @__PURE__ */ Pe(n)
      );
      if (s.before(n), n === i)
        return;
      n = o;
    }
}
function De(e, t, r) {
  t === null ? e.effect.first = r : t.next = r, r === null ? e.effect.last = t : r.prev = t;
}
const _o = () => performance.now(), ze = {
  // don't access requestAnimationFrame eagerly outside method
  // this allows basic testing of user code without JSDOM
  // bunder will eval and remove ternary when the user's app is built
  tick: (
    /** @param {any} _ */
    (e) => requestAnimationFrame(e)
  ),
  now: () => _o(),
  tasks: /* @__PURE__ */ new Set()
};
function Hi() {
  const e = ze.now();
  ze.tasks.forEach((t) => {
    t.c(e) || (ze.tasks.delete(t), t.f());
  }), ze.tasks.size !== 0 && ze.tick(Hi);
}
function mo(e) {
  let t;
  return ze.tasks.size === 0 && ze.tick(Hi), {
    promise: new Promise((r) => {
      ze.tasks.add(t = { c: e, f: r });
    }),
    abort() {
      ze.tasks.delete(t);
    }
  };
}
function or(e, t) {
  Lr(() => {
    e.dispatchEvent(new CustomEvent(t));
  });
}
function wo(e) {
  if (e === "float") return "cssFloat";
  if (e === "offset") return "cssOffset";
  if (e.startsWith("--")) return e;
  const t = e.split("-");
  return t.length === 1 ? t[0] : t[0] + t.slice(1).map(
    /** @param {any} word */
    (r) => r[0].toUpperCase() + r.slice(1)
  ).join("");
}
function Mn(e) {
  const t = {}, r = e.split(";");
  for (const n of r) {
    const [i, s] = n.split(":");
    if (!i || s === void 0) break;
    const o = wo(i.trim());
    t[o] = s.trim();
  }
  return t;
}
const yo = (e) => e;
function bo(e, t, r, n) {
  var _;
  var i = (e & is) !== 0, s = "both", o, l = t.inert, f = t.style.overflow, u, v;
  function h() {
    return Lr(() => o ?? (o = r()(t, (n == null ? void 0 : n()) ?? /** @type {P} */
    {}, {
      direction: s
    })));
  }
  var c = {
    is_global: i,
    in() {
      t.inert = l, u = Jr(
        t,
        h(),
        v,
        1,
        () => {
          or(t, "introstart");
        },
        () => {
          or(t, "introend"), u == null || u.abort(), u = o = void 0, t.style.overflow = f;
        }
      );
    },
    out(w) {
      t.inert = !0, v = Jr(
        t,
        h(),
        u,
        0,
        () => {
          or(t, "outrostart");
        },
        () => {
          or(t, "outroend"), w == null || w();
        }
      );
    },
    stop: () => {
      u == null || u.abort(), v == null || v.abort();
    }
  }, p = (
    /** @type {Effect & { nodes: EffectNodes }} */
    k
  );
  if (((_ = p.nodes).t ?? (_.t = [])).push(c), Wr) {
    var d = i;
    if (!d) {
      for (var g = (
        /** @type {Effect | null} */
        p.parent
      ); g && (g.f & dt) !== 0; )
        for (; (g = g.parent) && (g.f & oe) === 0; )
          ;
      d = !g || (g.f & Ze) !== 0;
    }
    d && Js(() => {
      ji(() => c.in());
    });
  }
}
function Jr(e, t, r, n, i, s) {
  var o = n === 1;
  if (us(t)) {
    var l, f = !1;
    return at(() => {
      if (!f) {
        var w = t({ direction: o ? "in" : "out" });
        l = Jr(e, w, r, n, i, s);
      }
    }), {
      abort: () => {
        f = !0, l == null || l.abort();
      },
      deactivate: () => l.deactivate(),
      reset: () => l.reset(),
      t: () => l.t()
    };
  }
  if (r == null || r.deactivate(), !(t != null && t.duration) && !(t != null && t.delay))
    return i(), s(), {
      abort: _t,
      deactivate: _t,
      reset: _t,
      t: () => n
    };
  const { delay: u = 0, css: v, tick: h, easing: c = yo } = t;
  var p = [];
  if (o && r === void 0 && (h && h(0, 1), v)) {
    var d = Mn(v(0, 1));
    p.push(d, d);
  }
  var g = () => 1 - n, _ = e.animate(p, { duration: u, fill: "forwards" });
  return _.onfinish = () => {
    _.cancel(), i();
    var w = (r == null ? void 0 : r.t()) ?? 1 - n;
    r == null || r.abort();
    var y = n - w, S = (
      /** @type {number} */
      t.duration * Math.abs(y)
    ), O = [];
    if (S > 0) {
      var C = !1;
      if (v)
        for (var K = Math.ceil(S / 16.666666666666668), _e = 0; _e <= K; _e += 1) {
          var ee = w + y * c(_e / K), G = Mn(v(ee, 1 - ee));
          O.push(G), C || (C = G.overflow === "hidden");
        }
      C && (e.style.overflow = "hidden"), g = () => {
        var Se = (
          /** @type {number} */
          /** @type {globalThis.Animation} */
          _.currentTime
        );
        return w + y * c(Se / S);
      }, h && mo(() => {
        if (_.playState !== "running") return !1;
        var Se = g();
        return h(Se, 1 - Se), !0;
      });
    }
    _ = e.animate(O, { duration: S, fill: "forwards" }), _.onfinish = () => {
      g = () => n, h == null || h(n, 1 - n), s();
    };
  }, {
    abort: () => {
      _ && (_.cancel(), _.effect = null, _.onfinish = _t);
    },
    deactivate: () => {
      s = _t;
    },
    reset: () => {
      n === 0 && (h == null || h(1, 0));
    },
    t: () => g()
  };
}
function Bi(e) {
  var t, r, n = "";
  if (typeof e == "string" || typeof e == "number") n += e;
  else if (typeof e == "object") if (Array.isArray(e)) {
    var i = e.length;
    for (t = 0; t < i; t++) e[t] && (r = Bi(e[t])) && (n && (n += " "), n += r);
  } else for (r in e) e[r] && (n && (n += " "), n += r);
  return n;
}
function xo() {
  for (var e, t, r = 0, n = "", i = arguments.length; r < i; r++) (e = arguments[r]) && (t = Bi(e)) && (n && (n += " "), n += t);
  return n;
}
function ko(e) {
  return typeof e == "object" ? xo(e) : e ?? "";
}
const Ln = [...` 	
\r\f \v\uFEFF`];
function $o(e, t, r) {
  var n = e == null ? "" : "" + e;
  if (t && (n = n ? n + " " + t : t), r) {
    for (var i of Object.keys(r))
      if (r[i])
        n = n ? n + " " + i : i;
      else if (n.length)
        for (var s = i.length, o = 0; (o = n.indexOf(i, o)) >= 0; ) {
          var l = o + s;
          (o === 0 || Ln.includes(n[o - 1])) && (l === n.length || Ln.includes(n[l])) ? n = (o === 0 ? "" : n.substring(0, o)) + n.substring(l + 1) : o = l;
        }
  }
  return n === "" ? null : n;
}
function Nn(e, t, r, n, i, s) {
  var o = (
    /** @type {any} */
    e[jr]
  );
  if (M || o !== r || o === void 0) {
    var l = $o(r, n, s);
    (!M || l !== e.getAttribute("class")) && (l == null ? e.removeAttribute("class") : e.className = l), e[jr] = r;
  } else if (s && i !== s)
    for (var f in s) {
      var u = !!s[f];
      (i == null || u !== !!i[f]) && e.classList.toggle(f, u);
    }
  return s;
}
const Eo = Symbol("is custom element"), To = Symbol("is html"), So = _s ? "link" : "LINK";
function me(e, t, r, n) {
  var i = Co(e);
  M && (i[t] = e.getAttribute(t), t === "src" || t === "srcset" || t === "href" && e.nodeName === So) || i[t] !== (i[t] = r) && (t === "loading" && (e[vs] = r), r == null ? e.removeAttribute(t) : typeof r != "string" && Ao(e).includes(t) ? e[t] = r : e.setAttribute(t, r));
}
function Co(e) {
  return (
    /** @type {Record<string | symbol, unknown>} **/
    /** @type {any} */
    e[ar] ?? (e[ar] = {
      [Eo]: e.nodeName.includes("-"),
      [To]: e.namespaceURI === Yn
    })
  );
}
var On = /* @__PURE__ */ new Map();
function Ao(e) {
  var t = e.getAttribute("is") || e.nodeName, r = On.get(t);
  if (r) return r;
  On.set(t, r = []);
  for (var n, i = e, s = Element.prototype; s !== i; ) {
    n = ls(i);
    for (var o in n)
      n[o].set && // better safe than sorry, we don't want spread attributes to mess with HTML content
      o !== "innerHTML" && o !== "textContent" && o !== "innerText" && r.push(o);
    i = Gn(i);
  }
  return r;
}
function Rn(e, t, r, n) {
  var i = (
    /** @type {V} */
    n
  ), s = !0, o = () => (s && (s = !1, i = /** @type {V} */
  n), i);
  e[t];
  var l;
  l = () => {
    var h = (
      /** @type {V} */
      e[t]
    );
    return h === void 0 ? o() : (s = !0, h);
  };
  var f = !1, u = /* @__PURE__ */ Mr(() => (f = !1, l())), v = (
    /** @type {Effect} */
    k
  );
  return (
    /** @type {() => V} */
    (function(h, c) {
      if (arguments.length > 0) {
        const p = c ? z(u) : h;
        return pe(u, p), f = !0, i !== void 0 && (i = p), h;
      }
      return Ke && f || (v.f & ae) !== 0 ? u.v : z(u);
    })
  );
}
function Mo(e) {
  return new Lo(e);
}
var Re, ie;
class Lo {
  /**
   * @param {ComponentConstructorOptions & {
   *  component: any;
   * }} options
   */
  constructor(t) {
    /** @type {any} */
    x(this, Re);
    /** @type {Record<string, any>} */
    x(this, ie);
    var s;
    var r = /* @__PURE__ */ new Map(), n = (o, l) => {
      var f = /* @__PURE__ */ _i(l, !1, !1);
      return r.set(o, f), f;
    };
    const i = new Proxy(
      { ...t.props || {}, $$events: {} },
      {
        get(o, l) {
          return z(r.get(l) ?? n(l, Reflect.get(o, l)));
        },
        has(o, l) {
          return l === ps ? !0 : (z(r.get(l) ?? n(l, Reflect.get(o, l))), Reflect.has(o, l));
        },
        set(o, l, f) {
          return pe(r.get(l) ?? n(l, f), f), Reflect.set(o, l, f);
        }
      }
    );
    m(this, ie, (t.hydrate ? uo : Di)(t.component, {
      target: t.target,
      anchor: t.anchor,
      props: i,
      context: t.context,
      intro: t.intro ?? !1,
      recover: t.recover,
      transformError: t.transformError
    })), (!((s = t == null ? void 0 : t.props) != null && s.$$host) || t.sync === !1) && Hr(), m(this, Re, i.$$events);
    for (const o of Object.keys(a(this, ie)))
      o === "$set" || o === "$destroy" || o === "$on" || vr(this, o, {
        get() {
          return a(this, ie)[o];
        },
        /** @param {any} value */
        set(l) {
          a(this, ie)[o] = l;
        },
        enumerable: !0
      });
    a(this, ie).$set = /** @param {Record<string, any>} next */
    (o) => {
      Object.assign(i, o);
    }, a(this, ie).$destroy = () => {
      co(a(this, ie));
    };
  }
  /** @param {Record<string, any>} props */
  $set(t) {
    a(this, ie).$set(t);
  }
  /**
   * @param {string} event
   * @param {(...args: any[]) => any} callback
   * @returns {any}
   */
  $on(t, r) {
    a(this, Re)[t] = a(this, Re)[t] || [];
    const n = (...i) => r.call(this, ...i);
    return a(this, Re)[t].push(n), () => {
      a(this, Re)[t] = a(this, Re)[t].filter(
        /** @param {any} fn */
        (i) => i !== n
      );
    };
  }
  $destroy() {
    a(this, ie).$destroy();
  }
}
Re = new WeakMap(), ie = new WeakMap();
let Vi;
typeof HTMLElement == "function" && (Vi = class extends HTMLElement {
  /**
   * @param {*} $$componentCtor
   * @param {*} $$slots
   * @param {ShadowRootInit | undefined} shadow_root_init
   */
  constructor(t, r, n) {
    super();
    /** The Svelte component constructor */
    T(this, "$$ctor");
    /** Slots */
    T(this, "$$s");
    /** @type {any} The Svelte component instance */
    T(this, "$$c");
    /** Whether or not the custom element is connected */
    T(this, "$$cn", !1);
    /** @type {Record<string, any>} Component props data */
    T(this, "$$d", {});
    /** `true` if currently in the process of reflecting component props back to attributes */
    T(this, "$$r", !1);
    /** @type {Record<string, CustomElementPropDefinition>} Props definition (name, reflected, type etc) */
    T(this, "$$p_d", {});
    /** @type {Record<string, EventListenerOrEventListenerObject[]>} Event listeners */
    T(this, "$$l", {});
    /** @type {Map<EventListenerOrEventListenerObject, Function>} Event listener unsubscribe functions */
    T(this, "$$l_u", /* @__PURE__ */ new Map());
    /** @type {any} The managed render effect for reflecting attributes */
    T(this, "$$me");
    /** @type {ShadowRoot | null} The ShadowRoot of the custom element */
    T(this, "$$shadowRoot", null);
    this.$$ctor = t, this.$$s = r, n && (this.$$shadowRoot = this.attachShadow(n));
  }
  /**
   * @param {string} type
   * @param {EventListenerOrEventListenerObject} listener
   * @param {boolean | AddEventListenerOptions} [options]
   */
  addEventListener(t, r, n) {
    if (this.$$l[t] = this.$$l[t] || [], this.$$l[t].push(r), this.$$c) {
      const i = this.$$c.$on(t, r);
      this.$$l_u.set(r, i);
    }
    super.addEventListener(t, r, n);
  }
  /**
   * @param {string} type
   * @param {EventListenerOrEventListenerObject} listener
   * @param {boolean | AddEventListenerOptions} [options]
   */
  removeEventListener(t, r, n) {
    if (super.removeEventListener(t, r, n), this.$$c) {
      const i = this.$$l_u.get(r);
      i && (i(), this.$$l_u.delete(r));
    }
  }
  async connectedCallback() {
    if (this.$$cn = !0, !this.$$c) {
      let r = function(s) {
        return (o) => {
          const l = $i("slot");
          s !== "default" && (l.name = s), Qe(o, l);
        };
      };
      var t = r;
      if (await Promise.resolve(), !this.$$cn || this.$$c)
        return;
      const n = {}, i = No(this);
      for (const s of this.$$s)
        s in i && (s === "default" && !this.$$d.children ? (this.$$d.children = r(s), n.default = !0) : n[s] = r(s));
      for (const s of this.attributes) {
        const o = this.$$g_p(s.name);
        o in this.$$d || (this.$$d[o] = hr(o, s.value, this.$$p_d, "toProp"));
      }
      for (const s in this.$$p_d)
        !(s in this.$$d) && this[s] !== void 0 && (this.$$d[s] = this[s], delete this[s]);
      this.$$c = Mo({
        component: this.$$ctor,
        target: this.$$shadowRoot || this,
        props: {
          ...this.$$d,
          $$slots: n,
          $$host: this
        }
      }), this.$$me = Zs(() => {
        Ti(() => {
          var s;
          this.$$r = !0;
          for (const o of pr(this.$$c)) {
            if (!((s = this.$$p_d[o]) != null && s.reflect)) continue;
            this.$$d[o] = this.$$c[o];
            const l = hr(
              o,
              this.$$d[o],
              this.$$p_d,
              "toAttribute"
            );
            l == null ? this.removeAttribute(this.$$p_d[o].attribute || o) : this.setAttribute(this.$$p_d[o].attribute || o, l);
          }
          this.$$r = !1;
        });
      });
      for (const s in this.$$l)
        for (const o of this.$$l[s]) {
          const l = this.$$c.$on(s, o);
          this.$$l_u.set(o, l);
        }
      this.$$l = {};
    }
  }
  // We don't need this when working within Svelte code, but for compatibility of people using this outside of Svelte
  // and setting attributes through setAttribute etc, this is helpful
  /**
   * @param {string} attr
   * @param {string} _oldValue
   * @param {string} newValue
   */
  attributeChangedCallback(t, r, n) {
    var i;
    this.$$r || (t = this.$$g_p(t), this.$$d[t] = hr(t, n, this.$$p_d, "toProp"), (i = this.$$c) == null || i.$set({ [t]: this.$$d[t] }));
  }
  disconnectedCallback() {
    this.$$cn = !1, Promise.resolve().then(() => {
      !this.$$cn && this.$$c && (this.$$c.$destroy(), this.$$me(), this.$$c = void 0);
    });
  }
  /**
   * @param {string} attribute_name
   */
  $$g_p(t) {
    return pr(this.$$p_d).find(
      (r) => this.$$p_d[r].attribute === t || !this.$$p_d[r].attribute && r.toLowerCase() === t
    ) || t;
  }
});
function hr(e, t, r, n) {
  var s;
  const i = (s = r[e]) == null ? void 0 : s.type;
  if (t = i === "Boolean" && typeof t != "boolean" ? t != null : t, !n || !r[e])
    return t;
  if (n === "toAttribute")
    switch (i) {
      case "Object":
      case "Array":
        return t == null ? null : JSON.stringify(t);
      case "Boolean":
        return t ? "" : null;
      case "Number":
        return t ?? null;
      default:
        return t;
    }
  else
    switch (i) {
      case "Object":
      case "Array":
        return t && JSON.parse(t);
      case "Boolean":
        return t;
      // conversion already handled above
      case "Number":
        return t != null ? +t : t;
      default:
        return t;
    }
}
function No(e) {
  const t = {};
  return e.childNodes.forEach((r) => {
    t[
      /** @type {Element} node */
      r.slot || "default"
    ] = !0;
  }), t;
}
function Yi(e, t, r, n, i, s) {
  let o = class extends Vi {
    constructor() {
      super(e, r, i), this.$$p_d = t;
    }
    static get observedAttributes() {
      return pr(t).map(
        (l) => (t[l].attribute || l).toLowerCase()
      );
    }
  };
  return pr(t).forEach((l) => {
    vr(o.prototype, l, {
      get() {
        return this.$$c && l in this.$$c ? this.$$c[l] : this.$$d[l];
      },
      set(f) {
        var h;
        f = hr(l, f, t), this.$$d[l] = f;
        var u = this.$$c;
        if (u) {
          var v = (h = bt(u, l)) == null ? void 0 : h.get;
          v ? u[l] = f : u.$set({ [l]: f });
        }
      }
    });
  }), n.forEach((l) => {
    vr(o.prototype, l, {
      get() {
        var f;
        return (f = this.$$c) == null ? void 0 : f[l];
      }
    });
  }), s && (o = s(o)), e.element = /** @type {any} */
  o, o;
}
const Oo = '*,:before,:after{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }::backdrop{--tw-border-spacing-x: 0;--tw-border-spacing-y: 0;--tw-translate-x: 0;--tw-translate-y: 0;--tw-rotate: 0;--tw-skew-x: 0;--tw-skew-y: 0;--tw-scale-x: 1;--tw-scale-y: 1;--tw-pan-x: ;--tw-pan-y: ;--tw-pinch-zoom: ;--tw-scroll-snap-strictness: proximity;--tw-gradient-from-position: ;--tw-gradient-via-position: ;--tw-gradient-to-position: ;--tw-ordinal: ;--tw-slashed-zero: ;--tw-numeric-figure: ;--tw-numeric-spacing: ;--tw-numeric-fraction: ;--tw-ring-inset: ;--tw-ring-offset-width: 0px;--tw-ring-offset-color: #fff;--tw-ring-color: rgb(59 130 246 / .5);--tw-ring-offset-shadow: 0 0 #0000;--tw-ring-shadow: 0 0 #0000;--tw-shadow: 0 0 #0000;--tw-shadow-colored: 0 0 #0000;--tw-blur: ;--tw-brightness: ;--tw-contrast: ;--tw-grayscale: ;--tw-hue-rotate: ;--tw-invert: ;--tw-saturate: ;--tw-sepia: ;--tw-drop-shadow: ;--tw-backdrop-blur: ;--tw-backdrop-brightness: ;--tw-backdrop-contrast: ;--tw-backdrop-grayscale: ;--tw-backdrop-hue-rotate: ;--tw-backdrop-invert: ;--tw-backdrop-opacity: ;--tw-backdrop-saturate: ;--tw-backdrop-sepia: ;--tw-contain-size: ;--tw-contain-layout: ;--tw-contain-paint: ;--tw-contain-style: }*,:before,:after{box-sizing:border-box;border-width:0;border-style:solid;border-color:currentColor}:before,:after{--tw-content: ""}html,:host{line-height:1.5;-webkit-text-size-adjust:100%;-moz-tab-size:4;-o-tab-size:4;tab-size:4;font-family:ui-sans-serif,system-ui,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji";font-feature-settings:normal;font-variation-settings:normal;-webkit-tap-highlight-color:transparent}body{margin:0;line-height:inherit}hr{height:0;color:inherit;border-top-width:1px}abbr:where([title]){-webkit-text-decoration:underline dotted;text-decoration:underline dotted}h1,h2,h3,h4,h5,h6{font-size:inherit;font-weight:inherit}a{color:inherit;text-decoration:inherit}b,strong{font-weight:bolder}code,kbd,samp,pre{font-family:ui-monospace,SFMono-Regular,Menlo,Monaco,Consolas,Liberation Mono,Courier New,monospace;font-feature-settings:normal;font-variation-settings:normal;font-size:1em}small{font-size:80%}sub,sup{font-size:75%;line-height:0;position:relative;vertical-align:baseline}sub{bottom:-.25em}sup{top:-.5em}table{text-indent:0;border-color:inherit;border-collapse:collapse}button,input,optgroup,select,textarea{font-family:inherit;font-feature-settings:inherit;font-variation-settings:inherit;font-size:100%;font-weight:inherit;line-height:inherit;letter-spacing:inherit;color:inherit;margin:0;padding:0}button,select{text-transform:none}button,input:where([type=button]),input:where([type=reset]),input:where([type=submit]){-webkit-appearance:button;background-color:transparent;background-image:none}:-moz-focusring{outline:auto}:-moz-ui-invalid{box-shadow:none}progress{vertical-align:baseline}::-webkit-inner-spin-button,::-webkit-outer-spin-button{height:auto}[type=search]{-webkit-appearance:textfield;outline-offset:-2px}::-webkit-search-decoration{-webkit-appearance:none}::-webkit-file-upload-button{-webkit-appearance:button;font:inherit}summary{display:list-item}blockquote,dl,dd,h1,h2,h3,h4,h5,h6,hr,figure,p,pre{margin:0}fieldset{margin:0;padding:0}legend{padding:0}ol,ul,menu{list-style:none;margin:0;padding:0}dialog{padding:0}textarea{resize:vertical}input::-moz-placeholder,textarea::-moz-placeholder{opacity:1;color:#9ca3af}input::placeholder,textarea::placeholder{opacity:1;color:#9ca3af}button,[role=button]{cursor:pointer}:disabled{cursor:default}img,svg,video,canvas,audio,iframe,embed,object{display:block;vertical-align:middle}img,video{max-width:100%;height:auto}[hidden]:where(:not([hidden=until-found])){display:none}.static{position:static}.absolute{position:absolute}.left-0{left:0}.top-14{top:3.5rem}.z-\\[1073\\]{z-index:1073}.mt-2{margin-top:.5rem}.inline{display:inline}.flex{display:flex}.grid{display:grid}.\\!hidden{display:none!important}.hidden{display:none}.h-6{height:1.5rem}.w-6{width:1.5rem}.max-w-\\[335px\\]{max-width:335px}.transform{transform:translate(var(--tw-translate-x),var(--tw-translate-y)) rotate(var(--tw-rotate)) skew(var(--tw-skew-x)) skewY(var(--tw-skew-y)) scaleX(var(--tw-scale-x)) scaleY(var(--tw-scale-y))}.grid-cols-\\[1fr_24px\\]{grid-template-columns:1fr 24px}.flex-col{flex-direction:column}.items-center{align-items:center}.justify-center{justify-content:center}.gap-1{gap:.25rem}.gap-2{gap:.5rem}.overflow-hidden{overflow:hidden}.rounded-lg{border-radius:.5rem}.border{border-width:1px}.border-eki-green{--tw-border-opacity: 1;border-color:rgb(46 125 50 / var(--tw-border-opacity, 1))}.border-eki-light-blue{--tw-border-opacity: 1;border-color:rgb(215 229 242 / var(--tw-border-opacity, 1))}.border-eki-red{--tw-border-opacity: 1;border-color:rgb(231 5 5 / var(--tw-border-opacity, 1))}.border-eki-warning{--tw-border-opacity: 1;border-color:rgb(133 100 4 / var(--tw-border-opacity, 1))}.bg-eki-light-green{--tw-bg-opacity: 1;background-color:rgb(247 253 249 / var(--tw-bg-opacity, 1))}.bg-eki-light-red{--tw-bg-opacity: 1;background-color:rgb(255 245 241 / var(--tw-bg-opacity, 1))}.bg-eki-light-warning{--tw-bg-opacity: 1;background-color:rgb(255 243 205 / var(--tw-bg-opacity, 1))}.bg-eki-white{--tw-bg-opacity: 1;background-color:rgb(255 255 255 / var(--tw-bg-opacity, 1))}.py-3{padding-top:.75rem;padding-bottom:.75rem}.pb-5{padding-bottom:1.25rem}.pl-6{padding-left:1.5rem}.pr-\\[10px\\]{padding-right:10px}.pt-3{padding-top:.75rem}.text-sm{font-size:.875rem;line-height:1.25rem}.font-medium{font-weight:500}.text-eki-dark-blue-text{--tw-text-opacity: 1;color:rgb(23 49 72 / var(--tw-text-opacity, 1))}.underline{text-decoration-line:underline}.filter{filter:var(--tw-blur) var(--tw-brightness) var(--tw-contrast) var(--tw-grayscale) var(--tw-hue-rotate) var(--tw-invert) var(--tw-saturate) var(--tw-sepia) var(--tw-drop-shadow)}*{font-family:Inter,-apple-system,blinkmacsystemfont,Segoe UI,roboto,Helvetica Neue,arial,Noto Sans,sans-serif,"Apple Color Emoji","Segoe UI Emoji",Segoe UI Symbol,"Noto Color Emoji"}.break-word{word-break:break-word}.hover\\:no-underline:hover{text-decoration-line:none}@media(min-width:320px){.min-\\[320px\\]\\:right-4{right:1rem}}', Xr = new CSSStyleSheet();
Xr.replaceSync(Oo);
const Ro = (e) => class extends e {
  connectedCallback() {
    var t, r;
    (t = super.connectedCallback) == null || t.call(this), (r = this.shadowRoot) != null && r.adoptedStyleSheets && Xr && (this.shadowRoot.adoptedStyleSheets = [Xr]);
  }
};
function zo(e) {
  const t = e - 1;
  return t * t * t + 1;
}
function zn(e) {
  const t = typeof e == "string" && e.match(/^\s*(-?[\d.]+)([^\s]*)\s*$/);
  return t ? [parseFloat(t[1]), t[2] || "px"] : [
    /** @type {number} */
    e,
    "px"
  ];
}
function qo(e, { delay: t = 0, duration: r = 400, easing: n = zo, x: i = 0, y: s = 0, opacity: o = 0 } = {}) {
  const l = getComputedStyle(e), f = +l.opacity, u = l.transform === "none" ? "" : l.transform, v = f * (1 - o), [h, c] = zn(i), [p, d] = zn(s);
  return {
    delay: t,
    duration: r,
    easing: n,
    css: (g, _) => `
			transform: ${u} translate(${(1 - g) * h}${c}, ${(1 - g) * p}${d});
			opacity: ${f - v * _}`
  };
}
var Io = /* @__PURE__ */ zt('<a class="underline hover:no-underline"> </a>'), jo = /* @__PURE__ */ zt('<p class="text-sm font-medium"> <!></p>'), Po = /* @__PURE__ */ zt('<a class="underline hover:no-underline"> </a>'), Do = /* @__PURE__ */ zt('<p class="break-word text-sm"><span> </span> <!></p>'), Fo = /* @__PURE__ */ zt('<section><div><!> <!></div> <button class="w-6 h-6 flex justify-center items-center" type="button"><svg width="14" height="14" viewBox="0 0 14 14" fill="none" xmlns="http://www.w3.org/2000/svg"><path d="M14 1.41L12.59 0L7 5.59L1.41 0L0 1.41L5.59 7L0 12.59L1.41 14L7 8.41L12.59 14L14 12.59L8.41 7L14 1.41Z" fill="currentColor"></path></svg></button></section>');
function en(e, t) {
  on(t, !0);
  let r = Rn(t, "toast"), n = Rn(t, "closeToast");
  var i = {
    get toast() {
      return r();
    },
    set toast(p) {
      r(p), Hr();
    },
    get closeToast() {
      return n();
    },
    set closeToast(p) {
      n(p), Hr();
    }
  }, s = Fo(), o = Fe(s);
  let l;
  var f = Fe(o);
  {
    var u = (p) => {
      var d = jo(), g = Fe(d), _ = Ft(g);
      {
        var w = (y) => {
          var S = Io(), O = Fe(S, !0);
          we(S), qt(() => {
            me(S, "href", r().readMoreUrl), me(S, "target", r().readMoreIsExternal ? "_blank" : void 0), me(S, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), nr(O, r().readMoreText);
          }), Qe(y, S);
        };
        sr(_, (y) => {
          !r().body && r().readMoreText && y(w);
        });
      }
      we(d), qt(() => {
        me(d, "id", `toast-title-${r().id}`), nr(g, `${r().title ?? ""} `);
      }), Qe(p, d);
    };
    sr(f, (p) => {
      r().title && p(u);
    });
  }
  var v = Ft(f, 2);
  {
    var h = (p) => {
      var d = Do(), g = Fe(d), _ = Fe(g, !0);
      we(g);
      var w = Ft(g, 2);
      {
        var y = (S) => {
          var O = Po(), C = Fe(O, !0);
          we(O), qt(() => {
            me(O, "href", r().readMoreUrl), me(O, "target", r().readMoreIsExternal ? "_blank" : void 0), me(O, "rel", r().readMoreIsExternal ? "noreferrer" : void 0), nr(C, r().readMoreText);
          }), Qe(S, O);
        };
        sr(w, (S) => {
          r().readMoreText && S(y);
        });
      }
      we(d), qt(() => {
        me(g, "id", `toast-body-${r().id}`), nr(_, r().body);
      }), Qe(p, d);
    };
    sr(v, (p) => {
      r().body && p(h);
    });
  }
  we(o);
  var c = Ft(o, 2);
  return we(s), qt(() => {
    Nn(s, 1, ko(r().class)), me(s, "aria-labelledby", `toast-title-${r().id} toast-body-${r().id}`), l = Nn(o, 1, "flex gap-1", null, l, { "flex-col": r().body, "mt-2": r().title }), me(c, "aria-label", r().closeLabel);
  }), io("click", c, () => n()(r())), bo(3, s, () => qo, () => ({ x: 100 })), Qe(e, s), ln(i);
}
so(["click"]);
Yi(en, { toast: {}, closeToast: {} }, [], [], { mode: "open" });
var Ho = /* @__PURE__ */ zt('<div class="absolute top-14 min-[320px]:right-4 overflow-hidden z-[1073] flex flex-col gap-2"><div class="flex flex-col gap-2" role="alert"></div> <div class="flex flex-col gap-2" role="status"></div></div>');
function Bo(e, t) {
  on(t, !0);
  const r = t.$$host;
  let n = /* @__PURE__ */ Ae(yt([]));
  const i = /* @__PURE__ */ xn(() => z(n).filter((p) => p.type && ["error", "warning"].includes(p.type))), s = /* @__PURE__ */ xn(() => z(n).filter((p) => !p.type || !["error", "warning"].includes(p.type)));
  let o = 0;
  const l = "border text-eki-dark-blue-text rounded-lg pl-6 pr-[10px] grid grid-cols-[1fr_24px] gap-2 max-w-[335px] left-0", f = (p) => {
    pe(n, z(n).filter((d) => d.id !== p.id), !0), r.dispatchEvent(new CustomEvent("eki-toast-closed", { bubbles: !0, composed: !0, detail: p }));
  }, u = (p) => {
    p.isVisible = !0, p.id ?? (p.id = o++);
    const d = [
      p.type === "error" && "bg-eki-light-red border-eki-red py-3",
      p.type === "success" && "bg-eki-light-green border-eki-green py-3",
      p.type === "warning" && "bg-eki-light-warning border-eki-warning py-3"
    ].filter(Boolean).join(" ") || "bg-eki-white border-eki-light-blue pt-3 pb-5";
    p.class = `${l} ${d}`, pe(n, [...z(n), p], !0), r.dispatchEvent(new CustomEvent("eki-toast-opened", { bubbles: !0, composed: !0, detail: p }));
  };
  r.addToast = u;
  var v = Ho(), h = Fe(v);
  An(h, 21, () => z(i), (p) => p.id, (p, d) => {
    en(p, {
      get toast() {
        return z(d);
      },
      closeToast: f
    });
  }), we(h);
  var c = Ft(h, 2);
  An(c, 21, () => z(s), (p) => p.id, (p, d) => {
    en(p, {
      get toast() {
        return z(d);
      },
      closeToast: f
    });
  }), we(c), we(v), Qe(e, v), ln();
}
customElements.define("eki-toast", Yi(Bo, {}, [], [], { mode: "open" }, Ro));
const Vo = ":host{--color-dark-blue: #173148;--color-gray-1000: #0e1013;--color-gray-400: #5d606e;--color-gray-200: #ccd9e0;--color-blue-300: #2c6fb6;--color-white: #fff;--color-hall-500: #f9f9f9;--color-olive-brown: #94690d;display:block;font-family:inherit}*,*:before,*:after{box-sizing:border-box}.etym-tree{position:relative;display:flex;flex-direction:column;padding:4px 0 8px;color:var(--color-dark-blue)}.etym-tree__header{display:flex;flex-direction:column;gap:4px;margin-bottom:8px}.etym-tree__header-row{display:flex;align-items:center;gap:8px}.etym-tree__type{font-size:14px;line-height:21px;color:var(--color-gray-1000)}.etym-tree__toggle{display:inline-flex;align-items:center;gap:4px;padding:0;border:0;background:none;color:var(--color-blue-300);font-size:12px;line-height:21px;cursor:pointer}.etym-tree__icon{flex:none}.etym-tree__hint{font-size:12px;line-height:21px;color:var(--color-gray-400)}.etym-tree__canvas{position:relative;width:100%;min-height:250px}.etym-tree__links{position:absolute;top:0;left:0;overflow:visible;pointer-events:none}.etym-tree__line{fill:none;stroke:var(--color-gray-200);stroke-width:2}.etym-tree__line--questionable{stroke:var(--color-olive-brown)}.etym-tree__junction circle{fill:var(--color-white);stroke:var(--color-gray-200);stroke-width:1}.etym-tree__junction--questionable circle{fill:var(--color-olive-brown);stroke:var(--color-olive-brown)}.etym-tree__junction-question{fill:var(--color-white);font-size:16px;font-weight:700}.etym-tree__junction--interactive{pointer-events:auto;cursor:pointer}.etym-tree__junction--interactive:focus{outline:none}.etym-tree__junction-icon{fill:var(--color-dark-blue)}.etym-tree__levels{display:flex;flex-direction:column;align-items:center;gap:40px}.etym-tree__level{display:flex;justify-content:center;align-items:flex-start;gap:20px}.etym-tree__level>*{max-width:100%;min-width:0}.etym-tree--comments-hidden .etym-node__comments{display:none}.etym-tree--comments-hidden .etym-node__head{border-bottom:1px solid var(--color-gray-200);border-radius:4px}.etym-tree__error{padding:8px;color:var(--color-gray-400)}.etym-node{display:flex;flex-direction:column;align-items:center}.etym-node--wide-comment .etym-node__comment:first-child{border-top-left-radius:4px;border-top-right-radius:4px}.etym-node__head{display:flex;align-items:stretch;overflow:hidden;border:1px solid var(--color-gray-200);border-bottom:0;border-radius:4px 4px 0 0;cursor:pointer}.etym-node--no-comment .etym-node__head{border-bottom:1px solid var(--color-gray-200);border-radius:4px}.etym-node__lang{display:flex;align-items:center;padding:2px 4px 3px 6px;background:var(--color-hall-500);border-right:1px solid var(--color-gray-200);font-size:14px;line-height:21px}.etym-node__value{display:flex;flex-direction:column;align-items:center;justify-content:center;padding:2px 6px 3px 4px;background:var(--color-white);font-size:14px;line-height:21px;text-align:center;overflow-wrap:anywhere;word-break:break-word}.etym-node__variant{color:var(--color-gray-400)}.etym-node__comments{width:100%}.etym-node__comment{padding:2px 6px 3px;border:1px solid var(--color-gray-200);font-size:12px;line-height:21px;text-align:center;overflow-wrap:anywhere;word-break:break-word}.etym-node__comment:not(:last-child){border-bottom:0}.etym-node__comment:last-child{border-bottom-left-radius:4px;border-bottom-right-radius:4px}.etym-label{padding:2px 8px 3px;background:var(--color-hall-500);border:1px solid var(--color-gray-200);border-radius:4px;font-size:14px;line-height:21px;color:var(--color-dark-blue);text-align:center}.etym-group{display:flex;flex-direction:column;align-items:center}.etym-group__name{align-self:stretch;padding:2px 8px 3px;background:var(--color-hall-500);border:1px solid var(--color-gray-200);border-radius:4px 4px 0 0;font-size:14px;line-height:21px;text-align:center}.etym-group__nodes{display:flex;flex-direction:column;align-items:center;gap:6px;padding:6px 20px;border:1px solid var(--color-gray-200);border-top:0;border-radius:0 0 4px 4px}.etym-questionable-connector{display:flex;flex-direction:column;align-items:center}.etym-questionable-connector__line{width:0;height:14px;border-left:2px solid var(--color-olive-brown)}.etym-questionable{display:inline-flex;align-items:center;justify-content:center;width:24px;height:24px;border-radius:50%;background:var(--color-olive-brown);color:var(--color-white);font-size:16px;font-weight:700;line-height:1;cursor:pointer;-webkit-user-select:none;-moz-user-select:none;user-select:none}.etym-questionable:focus{outline:none}.etym-questionable--inline{width:16px;height:16px;margin-left:4px;font-size:11px;vertical-align:middle}.etym-questionable__note{color:var(--color-olive-brown);font-style:italic}.etym-tip{position:absolute;z-index:20;display:none;max-width:320px;padding:10px 16px;border-radius:8px;background:var(--color-white);box-shadow:0 4px 16px #00000026;font-size:14px;line-height:21px;color:var(--color-dark-blue)}.etym-tip--visible{display:block}.etym-tip .etym-tooltip__row+.etym-tooltip__row{margin-top:16px}.etym-tip .etym-tooltip__label{font-size:10px;line-height:18px;text-transform:uppercase;color:var(--color-gray-400)}.etym-tip .etym-tooltip__value{display:flex;flex-direction:column;gap:12px;font-size:14px;line-height:21px;color:var(--color-dark-blue)}eki-foreign{color:#00874f;font-style:italic}eki-highlight{font-weight:700}eki-stress{font-weight:700;color:#ec0138}eki-sub{vertical-align:sub;font-size:70%}eki-sup{vertical-align:super;font-size:70%}eki-meta{color:#00874f;font-variant:small-caps;font-stretch:expanded}eki-link,ext-link{color:#004f87;cursor:pointer}";
function Ge(e) {
  const t = document.createElement("div");
  return t.textContent = e == null ? "" : String(e), t.innerHTML;
}
function We(e) {
  return Ge(e).replace(/"/g, "&quot;");
}
function Yo(e, t) {
  const r = e && e.levels || [], n = Uo(r), i = [];
  return n.forEach((s) => {
    const o = s.groups, l = o.find((f) => f.groupType === "COMPOUND");
    l ? i.push({
      level: s.level,
      connector: "merge",
      alternative: !1,
      items: (l.groupMembers || []).map((f) => tn(f, l, t))
    }) : i.push({
      level: s.level,
      connector: "chain",
      alternative: o.some((f) => f.groupType === "ALTERNATIVE"),
      items: Go(o, t)
    });
  }), { typeLabel: Ko(r), levels: i };
}
function Uo(e) {
  const t = [];
  return e.forEach((r) => {
    const n = t[t.length - 1];
    n && n.level === r.level ? n.groups.push(...r.groups || []) : t.push({ level: r.level, groups: [...r.groups || []] });
  }), t;
}
function Ko(e) {
  const t = e[0];
  return !t || !t.groups || !t.groups.length ? "" : (t.groups.find((n) => n.groupType === "ROOT") || t.groups[0]).etymologyTypeCode || "";
}
function Go(e, t) {
  const r = [];
  return e.forEach((n) => {
    n.groupType === "LANGUAGE_GROUP" ? r.push({
      kind: "group",
      name: n.languageGroupName,
      nodes: (n.groupMembers || []).map((i) => tn(i, void 0, t)),
      questionableTooltip: Ki(n, t)
    }) : (n.groupMembers || []).forEach((i) => r.push(tn(i, n, t)));
  }), r;
}
function Ui(e, t) {
  const r = e[t];
  return { html: '<span class="etym-questionable__note">' + Ge(r) + "</span>", text: r };
}
function Ki(e, t) {
  return !e || !e.questionable ? null : Ui(t, "lex.wordetym.questionable.tooltip");
}
function Wo(e, t) {
  return !e || !e.questionable ? null : Ui(t, "lex.wordetym.questionable.member");
}
function tn(e, t, r) {
  const n = {
    kind: "node",
    id: e.id,
    wordId: e.wordId,
    language: e.lang,
    langValue: e.langValue,
    year: e.etymologyYear,
    value: e.valuePrese,
    variants: (e.variantWords || []).map((i) => i.valuePrese).filter(Boolean),
    comments: (e.comments || []).map((i) => i.valuePrese).filter(Boolean),
    sources: (e.sourceLinks || []).map((i) => i.name || i.sourceName).filter(Boolean),
    notes: (e.notes || []).map((i) => i.valuePrese).filter(Boolean),
    questionableTooltip: Ki(t, r),
    selfQuestionableTip: Wo(e, r),
    detailTip: ""
  };
  return n.detailTip = nl(n, r), n;
}
function Zo(e, t) {
  return e.levels.length ? '<div class="etym-tree">' + Xo(e, t) + '<div class="etym-tree__canvas"><svg class="etym-tree__links" aria-hidden="true"></svg><div class="etym-tree__levels">' + e.levels.map(el).join("") + '</div></div><div class="etym-tip" role="tooltip"></div></div>' : "";
}
const Qo = '<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17a5 5 0 110-10 5 5 0 010 10zm0-8a3 3 0 100 6 3 3 0 000-6z"/></svg>', Jo = '<svg class="etym-tree__icon" viewBox="0 0 24 24" width="16" height="16" fill="currentColor" aria-hidden="true"><path d="M12 7a5 5 0 015 5c0 .65-.13 1.26-.36 1.83l2.92 2.92A11.8 11.8 0 0023 12c-1.73-4.39-6-7.5-11-7.5-1.4 0-2.74.25-3.98.7l2.16 2.16A5 5 0 0112 7zM2 4.27l2.28 2.28.46.46A11.8 11.8 0 001 12c1.73 4.39 6 7.5 11 7.5 1.55 0 3.03-.3 4.38-.84l.42.42L19.73 22 21 20.73 3.27 3 2 4.27zm5.53 5.53l1.55 1.55a3 3 0 003.65 3.65l1.55 1.55A5 5 0 017.53 9.8zM12 9a3 3 0 013 3l-3-3z"/></svg>';
function Gi(e, t) {
  const r = e ? Jo : Qo, n = e ? t["lex.wordetym.comments.hide"] : t["lex.wordetym.comments.show"];
  return r + '<span class="etym-tree__toggle-text">' + n + "</span>";
}
function Xo(e, t) {
  return '<div class="etym-tree__header"><div class="etym-tree__header-row">' + (e.typeLabel ? '<span class="etym-tree__type">' + Ge(e.typeLabel) + "</span>" : "") + '<button type="button" class="etym-tree__toggle" data-shown="true">' + Gi(!0, t) + '</button></div><div class="etym-tree__hint">' + (t["lex.wordetym.detail.hint"] || "") + "</div></div>";
}
function el(e, t) {
  const r = e.connector === "merge" ? ' data-connector="merge"' : "", n = t === 0;
  return '<div class="etym-tree__level"' + r + ">" + e.items.map((i) => tl(i, n)).join("") + "</div>";
}
function tl(e, t) {
  return e.kind === "group" ? rl(e, t) : e.kind === "label" ? '<div class="etym-label">' + Ge(e.text) + "</div>" : Wi(e, t);
}
function rl(e, t) {
  return '<div class="etym-group">' + (t ? Zi(e.questionableTooltip) : "") + '<div class="etym-group__name">' + Ge(e.name) + '</div><div class="etym-group__nodes">' + e.nodes.map((r) => Wi(r, !1)).join("") + "</div></div>";
}
function Wi(e, t) {
  const r = e.comments.length > 0, n = e.variants.map((o) => '<span class="etym-node__variant">' + o + "</span>").join(""), i = '<div class="etym-node__head" tabindex="0" role="button" data-tip="' + We(e.detailTip) + '"><span class="etym-node__lang">' + Ge(e.language) + '</span><span class="etym-node__value"><span class="etym-node__word">' + (e.value || "") + il(e.selfQuestionableTip) + "</span>" + n + "</span></div>", s = r ? '<div class="etym-node__comments">' + e.comments.map((o) => '<div class="etym-node__comment">' + o + "</div>").join("") + "</div>" : "";
  return '<div class="etym-node' + (r ? "" : " etym-node--no-comment") + '">' + (t ? Zi(e.questionableTooltip) : "") + i + s + "</div>";
}
function nl(e, t) {
  const r = [lr(t["lex.wordetym.lang"], [Ge(e.langValue)])];
  return e.year && r.push(lr(t["lex.wordetym.year"], [Ge(e.year)])), e.sources.length && r.push(lr(t["lex.source.link"], e.sources)), e.notes.length && r.push(lr(t["lex.wordetym.notes"], e.notes)), r.join("");
}
function lr(e, t) {
  const r = t.map((n) => '<div class="etym-tooltip__item">' + n + "</div>").join("");
  return '<div class="etym-tooltip__row"><div class="etym-tooltip__label">' + (e || "") + '</div><div class="etym-tooltip__value">' + r + "</div></div>";
}
function il(e) {
  return e ? '<span class="etym-questionable etym-questionable--inline" tabindex="0" role="button" aria-label="' + We(e.text) + '" data-tip="' + We(e.html) + '">?</span>' : "";
}
function Zi(e) {
  return e ? '<div class="etym-questionable-connector"><span class="etym-questionable" tabindex="0" role="button" aria-label="' + We(e.text) + '" data-tip="' + We(e.html) + '">?</span><span class="etym-questionable-connector__line" aria-hidden="true"></span></div>' : "";
}
const sl = "M4.425 10.5C4.425 9.2175 5.4675 8.175 6.75 8.175H9.75V6.75H6.75C4.68 6.75 3 8.43 3 10.5 3 12.57 4.68 14.25 6.75 14.25H9.75V12.825H6.75C5.4675 12.825 4.425 11.7825 4.425 10.5ZM7.5 11.25H13.5V9.75H7.5V11.25ZM14.25 6.75H11.25V8.175H14.25C15.5325 8.175 16.575 9.2175 16.575 10.5 16.575 11.7825 15.5325 12.825 14.25 12.825H11.25V14.25H14.25C16.32 14.25 18 12.57 18 10.5 18 8.43 16.32 6.75 14.25 6.75Z", ol = "M17 20.41 18.41 19 15 15.59 13.59 17 17 20.41ZM7.5 8H11v5.59L5.59 19 7 20.41l6-6V8h3.5L12 3.5 7.5 8Z";
function qn(e) {
  return e.reduce((t, r) => t + r, 0) / e.length;
}
function In(e, t, r, n, i = !1) {
  const s = (t + n) / 2;
  return '<path class="' + (i ? "etym-tree__line etym-tree__line--questionable" : "etym-tree__line") + '" d="M ' + e + " " + t + " C " + e + " " + s + ", " + r + " " + s + ", " + r + " " + n + '" />';
}
function ll(e, t, r, n) {
  const i = r === "merge", s = i ? ol : sl, o = i ? "translate(-8,-8) scale(0.667)" : "translate(-9.45,-9.45) scale(0.9)";
  return '<g class="etym-tree__junction' + (n ? ' etym-tree__junction--interactive" tabindex="0" role="button" data-tip="' + We(n) + '"' : '"') + ' transform="translate(' + e + "," + t + ')"><circle r="12" /><path class="etym-tree__junction-icon" transform="' + o + '" d="' + s + '" /></g>';
}
function al(e, t, r) {
  return '<g class="etym-tree__junction etym-tree__junction--interactive etym-tree__junction--questionable" tabindex="0" role="button" aria-label="' + We(r.text) + '" data-tip="' + We(r.html) + '" transform="translate(' + e + "," + t + ')"><circle r="12" /><text class="etym-tree__junction-question" text-anchor="middle" dominant-baseline="central">?</text></g>';
}
function fl(e) {
  if (!e)
    return null;
  for (const t of e.items)
    if ((t.kind === "node" || t.kind === "group") && t.questionableTooltip)
      return t.questionableTooltip;
  return null;
}
function jn(e) {
  for (let t = 0; t < e.length; t++) {
    const r = e[t];
    if (r.kind === "node")
      return r;
    if (r.kind === "group" && r.nodes && r.nodes.length)
      return r.nodes[0];
  }
  return null;
}
function Pn(e) {
  try {
    return JSON.parse(e);
  } catch {
    return null;
  }
}
class ul extends HTMLElement {
  constructor() {
    super();
    T(this, "root");
    T(this, "_data", null);
    T(this, "_messages", {});
    T(this, "tree", null);
    T(this, "resizeObserver", null);
    T(this, "drawFrame", 0);
    T(this, "listenersBound", !1);
    T(this, "onRootClick", (r) => {
      const n = this.tipEl(), i = r.composedPath();
      if (n && i.indexOf(n) !== -1)
        return;
      const s = i.find(
        (o) => o instanceof Element && o.hasAttribute("data-tip")
      );
      s ? this.toggleTip(s) : this.hideTip();
    });
    T(this, "onDocClick", (r) => {
      r.composedPath().indexOf(this) === -1 && this.hideTip();
    });
    T(this, "onKeyDown", (r) => {
      r.key === "Escape" && this.hideTip();
    });
    this.root = this.attachShadow({ mode: "open" }), this.root.addEventListener("click", this.onRootClick);
  }
  static get observedAttributes() {
    return ["data", "messages"];
  }
  set data(r) {
    this._data = typeof r == "string" ? Pn(r) : r, this.update();
  }
  get data() {
    return this._data;
  }
  set messages(r) {
    this._messages = typeof r == "string" ? Pn(r) || {} : r || {}, this.update();
  }
  get messages() {
    return this._messages;
  }
  attributeChangedCallback(r, n, i) {
    i != null && (r === "data" && (this.data = i), r === "messages" && (this.messages = i));
  }
  connectedCallback() {
    this.upgradeProperty("data"), this.upgradeProperty("messages"), this.listenersBound || (document.addEventListener("click", this.onDocClick, !0), document.addEventListener("keydown", this.onKeyDown), this.listenersBound = !0), this.update();
  }
  disconnectedCallback() {
    var r;
    (r = this.resizeObserver) == null || r.disconnect(), document.removeEventListener("click", this.onDocClick, !0), document.removeEventListener("keydown", this.onKeyDown), this.listenersBound = !1;
  }
  upgradeProperty(r) {
    if (Object.prototype.hasOwnProperty.call(this, r)) {
      const n = this[r];
      delete this[r], this[r] = n;
    }
  }
  update() {
    if (!this.isConnected || !this._data)
      return;
    this.tree = Yo(this._data, this._messages);
    const r = Zo(this.tree, this._messages);
    this.root.innerHTML = "<style>" + Vo + "</style>" + (r || ""), this.bindToggle(), this.scheduleDraw(), this.observeResize();
  }
  bindToggle() {
    const r = this.root.querySelector(".etym-tree__toggle"), n = this.root.querySelector(".etym-tree");
    !r || !n || r.addEventListener("click", () => {
      const s = !n.classList.toggle("etym-tree--comments-hidden");
      r.setAttribute("data-shown", String(s)), r.innerHTML = Gi(s, this._messages), this.scheduleDraw();
    });
  }
  observeResize() {
    var n;
    if (typeof ResizeObserver > "u") return;
    const r = this.root.querySelector(".etym-tree__canvas");
    r && ((n = this.resizeObserver) == null || n.disconnect(), this.resizeObserver = new ResizeObserver(() => this.scheduleDraw()), this.resizeObserver.observe(r));
  }
  scheduleDraw() {
    this.drawFrame && cancelAnimationFrame(this.drawFrame), this.drawFrame = requestAnimationFrame(() => {
      this.drawFrame = requestAnimationFrame(() => {
        this.markWideComments(), this.drawConnectors();
      });
    });
  }
  markWideComments() {
    this.root.querySelectorAll(".etym-node").forEach((r) => {
      const n = r.querySelector(".etym-node__head"), i = r.querySelector(".etym-node__comment"), s = !!n && !!i && i.offsetWidth > n.offsetWidth + 1;
      r.classList.toggle("etym-node--wide-comment", s);
    });
  }
  drawConnectors() {
    const r = this.root.querySelector(".etym-tree__canvas"), n = this.root.querySelector(".etym-tree__links"), i = this.root.querySelector(".etym-tree__levels");
    if (!r || !n || !i) return;
    const s = Array.from(i.children), o = r.clientWidth, l = r.clientHeight;
    if (n.setAttribute("width", String(o)), n.setAttribute("height", String(l)), n.setAttribute("viewBox", "0 0 " + o + " " + l), s.length < 2) {
      n.innerHTML = "";
      return;
    }
    const f = r.getBoundingClientRect(), u = (c) => Array.from(c.children).map((p) => {
      const d = p.getBoundingClientRect();
      return {
        x: d.left + d.width / 2 - f.left,
        top: d.top - f.top,
        bottom: d.bottom - f.top
      };
    }), v = this.tree && this.tree.levels || [], h = [];
    for (let c = 0; c < s.length - 1; c++) {
      const p = u(s[c]), d = u(s[c + 1]);
      if (!p.length || !d.length) continue;
      const g = (qn(p.map((C) => C.x)) + qn(d.map((C) => C.x))) / 2, _ = Math.max.apply(null, p.map((C) => C.bottom)), w = Math.min.apply(null, d.map((C) => C.top)), y = (_ + w) / 2, S = s[c + 1].getAttribute("data-connector") === "merge" ? "merge" : "chain", O = fl(v[c + 1]);
      if (p.forEach((C) => h.push(In(C.x, C.bottom, g, y, !!O))), d.forEach((C) => h.push(In(g, y, C.x, C.top, !!O))), O)
        h.push(al(g, y, O));
      else {
        const C = v[c] && jn(v[c].items), K = v[c + 1] && jn(v[c + 1].items), _e = C && K && this._messages["lex.wordetym.origin.link"] || "";
        h.push(ll(g, y, S, _e));
      }
    }
    n.innerHTML = h.join("");
  }
  tipEl() {
    return this.root.querySelector(".etym-tip");
  }
  toggleTip(r) {
    const n = this.tipEl();
    if (!n) return;
    n.classList.contains("etym-tip--visible") && n.dataset.for === Dn(r) ? this.hideTip() : this.showTip(r);
  }
  showTip(r) {
    const n = this.tipEl(), i = this.root.querySelector(".etym-tree");
    if (!n || !i) return;
    const s = r.getAttribute("data-tip") || "";
    n.innerHTML = s, n.dataset.for = Dn(r), n.classList.add("etym-tip--visible");
    const o = i.getBoundingClientRect(), l = r.getBoundingClientRect(), f = n.getBoundingClientRect();
    let u = l.left - o.left + l.width / 2 - f.width / 2;
    u = Math.max(0, Math.min(u, o.width - f.width));
    let v = l.top - o.top - f.height - 8;
    v < 0 && (v = l.bottom - o.top + 8), n.style.left = u + "px", n.style.top = v + "px";
  }
  hideTip() {
    const r = this.tipEl();
    r && (r.classList.remove("etym-tip--visible"), delete r.dataset.for);
  }
}
let cl = 0;
function Dn(e) {
  const t = e;
  return t.dataset.tipKey || (t.dataset.tipKey = String(++cl)), t.dataset.tipKey;
}
customElements.get("eki-etym-tree") || customElements.define("eki-etym-tree", ul);
