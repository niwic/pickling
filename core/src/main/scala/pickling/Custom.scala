package scala.pickling

import scala.pickling.internal._

import scala.language.experimental.macros
import scala.language.higherKinds

import scala.reflect.runtime.{universe => ru}
import scala.reflect.runtime.universe._

import scala.collection.immutable
import scala.collection.mutable
import scala.collection.generic.CanBuildFrom

import scala.collection.IndexedSeq
import scala.collection.LinearSeq
import immutable.:: //TODO: this should go away
import mutable.ArrayBuffer

class PicklerUnpicklerNotFound[T] extends SPickler[T] with Unpickler[T] {
  val format = null // not used
  def pickle(picklee: T, builder: PBuilder): Unit = ???
  def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = ???
}

trait LowPriorityPicklersUnpicklers {

  // collections

  implicit def iterablePickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[Iterable[T]], format: PickleFormat, cbf: CanBuildFrom[Iterable[T], T, Iterable[T]]): SPickler[Iterable[T]] with Unpickler[Iterable[T]] =
    mkTravPickler[T, Iterable[T]]

  implicit def seqPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[Seq[T]], format: PickleFormat, cbf: CanBuildFrom[Seq[T], T, Seq[T]]): SPickler[Seq[T]] with Unpickler[Seq[T]] =
    mkSeqSetPickler[T, Seq]

  implicit def indexedSeqPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[IndexedSeq[T]], format: PickleFormat, cbf: CanBuildFrom[IndexedSeq[T], T, IndexedSeq[T]]): SPickler[IndexedSeq[T]] with Unpickler[IndexedSeq[T]] =
    mkSeqSetPickler[T, IndexedSeq]

  implicit def linearSeqPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[LinearSeq[T]], format: PickleFormat, cbf: CanBuildFrom[LinearSeq[T], T, LinearSeq[T]]): SPickler[LinearSeq[T]] with Unpickler[LinearSeq[T]] =
    mkSeqSetPickler[T, LinearSeq]

  // immutable collections

  implicit def vectorPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[Vector[T]], format: PickleFormat, cbf: CanBuildFrom[Vector[T], T, Vector[T]]): SPickler[Vector[T]] with Unpickler[Vector[T]] =
    mkSeqSetPickler[T, Vector]

  // TODO: use this instead of the genListPickler
  // implicit def listPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[::[T]], format: PickleFormat, cbf: CanBuildFrom[::[T], T, ::[T]]): SPickler[::[T]] with Unpickler[::[T]] =
  //   mkSeqSetPickler[T, ::]

  // mutable collections

  implicit def arrayPickler[T >: Null: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[Array[T]], format: PickleFormat, cbf: CanBuildFrom[Array[T], T, Array[T]]): SPickler[Array[T]] with Unpickler[Array[T]] =
    mkTravPickler[T, Array[T]]

  implicit def arrayBufferPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[ArrayBuffer[T]], format: PickleFormat, cbf: CanBuildFrom[ArrayBuffer[T], T, ArrayBuffer[T]]): SPickler[ArrayBuffer[T]] with Unpickler[ArrayBuffer[T]] =
    mkSeqSetPickler[T, ArrayBuffer]

  // sets

  implicit def immSetPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[Set[T]], format: PickleFormat, cbf: CanBuildFrom[Set[T], T, Set[T]]): SPickler[Set[T]] with Unpickler[Set[T]] =
    mkSeqSetPickler[T, Set]

  implicit def immSortedSetPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[immutable.SortedSet[T]], format: PickleFormat, cbf: CanBuildFrom[immutable.SortedSet[T], T, immutable.SortedSet[T]]): SPickler[immutable.SortedSet[T]] with Unpickler[immutable.SortedSet[T]] =
    mkSeqSetPickler[T, immutable.SortedSet]

  implicit def mutSetPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[mutable.Set[T]], format: PickleFormat, cbf: CanBuildFrom[mutable.Set[T], T, mutable.Set[T]]): SPickler[mutable.Set[T]] with Unpickler[mutable.Set[T]] =
    mkSeqSetPickler[T, mutable.Set]

  implicit def mutSortedSetPickler[T: FastTypeTag](implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T], collTag: FastTypeTag[mutable.SortedSet[T]], format: PickleFormat, cbf: CanBuildFrom[mutable.SortedSet[T], T, mutable.SortedSet[T]]): SPickler[mutable.SortedSet[T]] with Unpickler[mutable.SortedSet[T]] =
    mkSeqSetPickler[T, mutable.SortedSet]

  // maps

  implicit def immMapPickler[K: FastTypeTag, V: FastTypeTag](implicit elemPickler: SPickler[(K, V)], elemUnpickler: Unpickler[(K, V)], pairTag: FastTypeTag[(K, V)], collTag: FastTypeTag[Map[K, V]], format: PickleFormat, cbf: CanBuildFrom[Map[K, V], (K, V), Map[K, V]]): SPickler[Map[K, V]] with Unpickler[Map[K, V]] =
    mkMapPickler[K, V, Map]

  implicit def immSortedMapPickler[K: FastTypeTag, V: FastTypeTag](implicit elemPickler: SPickler[(K, V)], elemUnpickler: Unpickler[(K, V)], pairTag: FastTypeTag[(K, V)], collTag: FastTypeTag[immutable.SortedMap[K, V]], format: PickleFormat, cbf: CanBuildFrom[immutable.SortedMap[K, V], (K, V), immutable.SortedMap[K, V]]): SPickler[immutable.SortedMap[K, V]] with Unpickler[immutable.SortedMap[K, V]] =
    mkMapPickler[K, V, immutable.SortedMap]

  implicit def mutMapPickler[K: FastTypeTag, V: FastTypeTag](implicit elemPickler: SPickler[(K, V)], elemUnpickler: Unpickler[(K, V)], pairTag: FastTypeTag[(K, V)], collTag: FastTypeTag[mutable.Map[K, V]], format: PickleFormat, cbf: CanBuildFrom[mutable.Map[K, V], (K, V), mutable.Map[K, V]]): SPickler[mutable.Map[K, V]] with Unpickler[mutable.Map[K, V]] =
    mkMapPickler[K, V, mutable.Map]


  def mkAnyRefArrayTravPickler[C <% Traversable[_]](mirror: ru.Mirror, classLoader: ClassLoader)(implicit pf: PickleFormat, cbf: CanBuildFrom[C, AnyRef, C]):
    SPickler[C] /*with Unpickler[C]*/ = new SPickler[C] /*with Unpickler[C]*/ {

    val format: PickleFormat = pf

    def pickle(coll: C, builder: PBuilder): Unit = {
      builder.hintTag(FastTypeTag.ArrayAnyRef)
      builder.beginEntry(coll)

      builder.beginCollection(coll.size)
      (coll: Traversable[_]).asInstanceOf[Traversable[AnyRef]].foreach { (elem: AnyRef) =>
        builder putElement { b =>
          val elemClass = elem.getClass
          val elemTag = FastTypeTag.mkRaw(elemClass, mirror) // slow: `mkRaw` is called for each element
          b.hintTag(elemTag)
          val pickler = SPickler.genPickler(classLoader, elemClass, elemTag).asInstanceOf[SPickler[AnyRef]]
          pickler.pickle(elem, b)
        }
      }
      builder.endCollection()

      builder.endEntry()
    }
  }

  def mkRuntimeTravPickler[C <% Traversable[_]](mirror: ru.Mirror, elemTag: FastTypeTag[_], collTag: FastTypeTag[_],
                                                  elemPickler0: SPickler[_], elemUnpickler0: Unpickler[_])
                                                 (implicit pf: PickleFormat, cbf: CanBuildFrom[C, AnyRef, C]):
    SPickler[C] with Unpickler[C] = new SPickler[C] with Unpickler[C] {

    val format: PickleFormat = pf

    val elemPickler   = elemPickler0.asInstanceOf[SPickler[AnyRef]]
    val elemUnpickler = elemUnpickler0.asInstanceOf[Unpickler[AnyRef]]

    def pickle(coll: C, builder: PBuilder): Unit = {
      builder.hintTag(collTag)
      builder.beginEntry(coll)

      builder.beginCollection(coll.size)
      (coll: Traversable[_]).asInstanceOf[Traversable[AnyRef]].foreach { (elem: AnyRef) =>
        builder putElement { b =>
          b.hintTag(elemTag)
          elemPickler.pickle(elem, b)
        }
      }
      builder.endCollection()

      builder.endEntry()
    }

    def unpickle(tpe: => FastTypeTag[_], preader: PReader): Any = {
      val reader = preader.beginCollection()

      val length = reader.readLength()
      val builder = cbf.apply()
      var i = 0
      while (i < length) {
        val r = reader.readElement()
        r.beginEntryNoTag()
        val elem = elemUnpickler.unpickle(elemTag, r)
        r.endEntry()
        builder += elem.asInstanceOf[AnyRef]
        i = i + 1
      }

      preader.endCollection()
      builder.result
    }
  }


  def mkTravPickler[T: FastTypeTag, C <% Traversable[_]: FastTypeTag]
    (implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T],
              pf: PickleFormat, cbf: CanBuildFrom[C, T, C],
              collTag: FastTypeTag[C]): SPickler[C] with Unpickler[C] =
    new SPickler[C] with Unpickler[C] {

    val format: PickleFormat = pf
    val elemTag  = implicitly[FastTypeTag[T]]
    val isPrimitive = elemTag.tpe.isEffectivelyPrimitive

    def pickle(coll: C, builder: PBuilder): Unit = {
      builder.hintTag(collTag)
      if (elemTag == FastTypeTag.Int) builder.hintKnownSize(coll.size * 4 + 100)
      builder.beginEntry(coll)

      builder.beginCollection(coll.size)

      if (isPrimitive) {
        builder.hintStaticallyElidedType()
        builder.hintTag(elemTag)
        builder.pinHints()
      }

      (coll: Traversable[_]).asInstanceOf[Traversable[T]].foreach { (elem: T) =>
        builder putElement { b =>
          if (!isPrimitive) b.hintTag(elemTag)
          elemPickler.pickle(elem, b)
        }
      }

      if (isPrimitive) builder.unpinHints()
      builder.endCollection()
      builder.endEntry()
    }

    def unpickle(tpe: => FastTypeTag[_], preader: PReader): Any = {
      val reader = preader.beginCollection()

      if (isPrimitive) {
        reader.hintStaticallyElidedType()
        reader.hintTag(elemTag)
        reader.pinHints()
      }

      val length = reader.readLength()
      val builder = cbf.apply() // builder with element type T
      var i = 0
      while (i < length) {
        val r = reader.readElement()
        r.beginEntryNoTag()
        val elem = elemUnpickler.unpickle(elemTag, r)
        r.endEntry()
        builder += elem.asInstanceOf[T]
        i = i + 1
      }

      if (isPrimitive) reader.unpinHints()
      preader.endCollection()
      builder.result
    }
  }

  def mkMapPickler[K: FastTypeTag, V: FastTypeTag, M[_, _] <: collection.Map[_, _]]
    (implicit elemPickler: SPickler[(K, V)], elemUnpickler: Unpickler[(K, V)],
              pf: PickleFormat, cbf: CanBuildFrom[M[K, V], (K, V), M[K, V]],
              pairTag: FastTypeTag[(K, V)], collTag: FastTypeTag[M[K, V]]): SPickler[M[K, V]] with Unpickler[M[K, V]] =
    mkTravPickler[(K, V), M[K, V]]

  def mkSeqSetPickler[T: FastTypeTag, Coll[_] <: Traversable[_]]
    (implicit elemPickler: SPickler[T], elemUnpickler: Unpickler[T],
              pf: PickleFormat, cbf: CanBuildFrom[Coll[T], T, Coll[T]],
              collTag: FastTypeTag[Coll[T]]): SPickler[Coll[T]] with Unpickler[Coll[T]] =
    mkTravPickler[T, Coll[T]]
}

trait CollectionPicklerUnpicklerMacro extends Macro {
  def mkType(eltpe: c.Type): c.Type
  def mkArray(picklee: c.Tree): c.Tree
  def mkBuffer(eltpe: c.Type): c.Tree
  def mkResult(buffer: c.Tree): c.Tree

  def impl[T: c.WeakTypeTag](format: c.Tree): c.Tree = {
    import c.universe._
    import definitions._
    val tpe = mkType(weakTypeOf[T])
    val eltpe = weakTypeOf[T]
    val isPrimitive = eltpe.isEffectivelyPrimitive
    val isFinal = eltpe.isEffectivelyFinal
    val picklerUnpicklerName = c.fresh(syntheticPicklerUnpicklerName(tpe).toTermName)
    q"""
      implicit object $picklerUnpicklerName extends scala.pickling.SPickler[$tpe] with scala.pickling.Unpickler[$tpe] {
        import scala.reflect.runtime.universe._
        import scala.pickling._
        import scala.pickling.internal._
        import scala.pickling.`package`.PickleOps

        val format = implicitly[${format.tpe}]

        val elpickler: SPickler[$eltpe] = {
          val elpickler = "bam!"
          implicitly[SPickler[$eltpe]]
        }
        val elunpickler: Unpickler[$eltpe] = {
          val elunpickler = "bam!"
          implicitly[Unpickler[$eltpe]]
        }
        val eltag: scala.pickling.FastTypeTag[$eltpe] = {
          val eltag = "bam!"
          implicitly[scala.pickling.FastTypeTag[$eltpe]]
        }
        val colltag: scala.pickling.FastTypeTag[$tpe] = {
          val colltag = "bam!"
          implicitly[scala.pickling.FastTypeTag[$tpe]]
        }

        def pickle(picklee: $tpe, builder: PBuilder): Unit = {
          builder.hintTag(colltag)
          ${
            if (eltpe =:= IntTpe) q"builder.hintKnownSize(picklee.length * 4 + 100)".asInstanceOf[Tree]
            else q"".asInstanceOf[Tree]
          }
          builder.beginEntry(picklee)
          ${
            if (isPrimitive) q"builder.hintStaticallyElidedType(); builder.hintTag(eltag); builder.pinHints()".asInstanceOf[Tree]
            else q"".asInstanceOf[Tree]
          }
          val arr = ${mkArray(q"picklee")}
          val length = arr.length
          builder.beginCollection(arr.length)
          var i = 0
          while (i < arr.length) {
            builder putElement { b =>
              ${
                if (!isPrimitive && !isFinal) q"""
                  b.hintTag(eltag)
                  arr(i).pickleInto(b)
                """.asInstanceOf[Tree] else if (!isPrimitive && isFinal) q"""
                  b.hintTag(eltag)
                  b.hintStaticallyElidedType()
                  arr(i).pickleInto(b)
                """.asInstanceOf[Tree] else q"""
                  elpickler.pickle(arr(i), b)
                """.asInstanceOf[Tree]
              }
            }
            i += 1
          }
          ${
            if (isPrimitive) q"builder.unpinHints()".asInstanceOf[Tree]
            else q"".asInstanceOf[Tree]
          }
          builder.endCollection()
          builder.endEntry()
        }
        def unpickle(tag: => scala.pickling.FastTypeTag[_], reader: PReader): Any = {
          val arrReader = reader.beginCollection()
          ${
            if (isPrimitive) q"arrReader.hintStaticallyElidedType(); arrReader.hintTag(eltag); arrReader.pinHints()".asInstanceOf[Tree]
            else q"".asInstanceOf[Tree]
          }
          val length = arrReader.readLength()
          var buffer = ${mkBuffer(eltpe)}
          var i = 0
          while (i < length) {
            val r = arrReader.readElement()
            ${
              if (isPrimitive) q"""
                r.beginEntryNoTag()
                val elem = elunpickler.unpickle(eltag, r).asInstanceOf[$eltpe]
                r.endEntry()
                buffer += elem
              """.asInstanceOf[Tree] else q"""
                val elem = r.unpickle[$eltpe]
                buffer += elem
              """.asInstanceOf[Tree]
            }
            i += 1
          }
          ${
            if (isPrimitive) q"arrReader.unpinHints()".asInstanceOf[Tree]
            else q"".asInstanceOf[Tree]
          }
          arrReader.endCollection()
          ${mkResult(q"buffer")}
        }
      }
      $picklerUnpicklerName
    """
  }
}

trait CorePicklersUnpicklers extends LowPriorityPicklersUnpicklers {
  import java.math.{BigDecimal, BigInteger}
  import java.util.{Date, TimeZone}
  import java.text.SimpleDateFormat

  implicit object BigDecimalPicklerUnpickler extends SPickler[BigDecimal] with Unpickler[BigDecimal] {
    val format = null // not used
    def pickle(picklee: BigDecimal, builder: PBuilder): Unit = {
      builder.beginEntry(picklee)

      builder.putField("value", b => {
        b.hintTag(implicitly[FastTypeTag[String]])
        b.hintStaticallyElidedType()
        stringPicklerUnpickler.pickle(picklee.toString, b)
      })

      builder.endEntry()
    }
    def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = {
      val reader1 = reader.readField("value")
      reader1.hintTag(implicitly[FastTypeTag[String]])
      reader1.hintStaticallyElidedType()

      val tag = reader1.beginEntry()
      val result = stringPicklerUnpickler.unpickle(tag, reader1)
      reader1.endEntry()

      new BigDecimal(result.asInstanceOf[String])
    }
  }

  implicit object BigIntPicklerUnpickler extends SPickler[BigInteger] with Unpickler[BigInteger] {
    val format = null // not used
    def pickle(picklee: BigInteger, builder: PBuilder): Unit = {
      builder.beginEntry(picklee)

      builder.putField("value", b => {
        b.hintTag(implicitly[FastTypeTag[String]])
        b.hintStaticallyElidedType()
        stringPicklerUnpickler.pickle(picklee.toString, b)
      })

      builder.endEntry()
    }
    def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = {
      val reader1 = reader.readField("value")
      reader1.hintTag(implicitly[FastTypeTag[String]])
      reader1.hintStaticallyElidedType()

      val tag = reader1.beginEntry()
      val result = stringPicklerUnpickler.unpickle(tag, reader1)
      reader1.endEntry()

      new BigInteger(result.asInstanceOf[String])
    }
  }

  implicit object DatePicklerUnpickler extends SPickler[Date] with Unpickler[Date] {
    private val dateFormatTemplate = {
      val format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'") //use ISO_8601 format
      format.setLenient(false)
      format.setTimeZone(TimeZone.getTimeZone("UTC"))
      format
    }
    private def dateFormat = dateFormatTemplate.clone.asInstanceOf[SimpleDateFormat]

    val format = null // not used
    def pickle(picklee: Date, builder: PBuilder): Unit = {
      builder.beginEntry(picklee)

      builder.putField("value", b => {
        b.hintTag(implicitly[FastTypeTag[String]])
        b.hintStaticallyElidedType()
        stringPicklerUnpickler.pickle(dateFormat.format(picklee), b)
      })

      builder.endEntry()
    }
    def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = {
      val reader1 = reader.readField("value")
      reader1.hintTag(implicitly[FastTypeTag[String]])
      reader1.hintStaticallyElidedType()

      val tag = reader1.beginEntry()
      val result = stringPicklerUnpickler.unpickle(tag, reader1)
      reader1.endEntry()

      dateFormat.parse(result.asInstanceOf[String])
    }
  }

  abstract class AutoRegister[T: FastTypeTag](name: String) extends SPickler[T] with Unpickler[T] {
    debug(s"autoregistering pickler $this under key '$name'")
    GlobalRegistry.picklerMap += (name -> this)
    val tag = implicitly[FastTypeTag[T]]
    debug(s"autoregistering unpickler $this under key '${tag.key}'")
    GlobalRegistry.unpicklerMap += (tag.key -> this)
  }

  class PrimitivePicklerUnpickler[T: FastTypeTag](name: String) extends AutoRegister[T](name) {
    val format = null // not used
    def pickle(picklee: T, builder: PBuilder): Unit = {
      builder.beginEntry(picklee)
      builder.endEntry()
    }
    def unpickle(tag: => FastTypeTag[_], reader: PReader): Any = {
      reader.readPrimitive()
    }
  }

  def mkPrimitivePicklerUnpickler[T: FastTypeTag]: SPickler[T] with Unpickler[T] =
    new PrimitivePicklerUnpickler[T](FastTypeTag.valueTypeName(implicitly[FastTypeTag[T]]))

  // TODO: figure out why removing these pickler/unpicklers slows down evactor1
  implicit val bytePicklerUnpickler: SPickler[Byte] with Unpickler[Byte] = mkPrimitivePicklerUnpickler[Byte]
  implicit val shortPicklerUnpickler: SPickler[Short] with Unpickler[Short] = mkPrimitivePicklerUnpickler[Short]
  implicit val charPicklerUnpickler: SPickler[Char] with Unpickler[Char] = mkPrimitivePicklerUnpickler[Char]
  implicit val intPicklerUnpickler: SPickler[Int] with Unpickler[Int] = mkPrimitivePicklerUnpickler[Int]
  implicit val longPicklerUnpickler: SPickler[Long] with Unpickler[Long] = mkPrimitivePicklerUnpickler[Long]
  implicit val booleanPicklerUnpickler: SPickler[Boolean] with Unpickler[Boolean] = mkPrimitivePicklerUnpickler[Boolean]
  implicit val floatPicklerUnpickler: SPickler[Float] with Unpickler[Float] = mkPrimitivePicklerUnpickler[Float]
  implicit val doublePicklerUnpickler: SPickler[Double] with Unpickler[Double] = mkPrimitivePicklerUnpickler[Double]
  implicit val nullPicklerUnpickler: SPickler[Null] with Unpickler[Null] = mkPrimitivePicklerUnpickler[Null]
  implicit val stringPicklerUnpickler: SPickler[String] with Unpickler[String] = mkPrimitivePicklerUnpickler[String]
  implicit val unitPicklerUnpickler: SPickler[Unit] with Unpickler[Unit] = mkPrimitivePicklerUnpickler[Unit]

  implicit val byteArrPicklerUnpickler: SPickler[Array[Byte]] with Unpickler[Array[Byte]] = mkPrimitivePicklerUnpickler[Array[Byte]]
  implicit val shortArrPicklerUnpickler: SPickler[Array[Short]] with Unpickler[Array[Short]] = mkPrimitivePicklerUnpickler[Array[Short]]
  implicit val charArrPicklerUnpickler: SPickler[Array[Char]] with Unpickler[Array[Char]] = mkPrimitivePicklerUnpickler[Array[Char]]
  implicit val intArrPicklerUnpickler: SPickler[Array[Int]] with Unpickler[Array[Int]] = mkPrimitivePicklerUnpickler[Array[Int]]
  implicit val longArrPicklerUnpickler: SPickler[Array[Long]] with Unpickler[Array[Long]] = mkPrimitivePicklerUnpickler[Array[Long]]
  implicit val booleanArrPicklerUnpickler: SPickler[Array[Boolean]] with Unpickler[Array[Boolean]] = mkPrimitivePicklerUnpickler[Array[Boolean]]
  implicit val floatArrPicklerUnpickler: SPickler[Array[Float]] with Unpickler[Array[Float]] = mkPrimitivePicklerUnpickler[Array[Float]]
  implicit val doubleArrPicklerUnpickler: SPickler[Array[Double]] with Unpickler[Array[Double]] = mkPrimitivePicklerUnpickler[Array[Double]]

  implicit def refPickler: SPickler[refs.Ref] = throw new Error("cannot pickle refs") // TODO: make this a macro
  implicit val refUnpickler: Unpickler[refs.Ref] = mkPrimitivePicklerUnpickler[refs.Ref]

  // TODO: remove this in favor of the implicit listPickler above
  implicit def genListPickler[T](implicit format: PickleFormat): SPickler[::[T]] with Unpickler[::[T]] = macro Compat.ListPicklerUnpicklerMacro_impl[T]
}

trait ListPicklerUnpicklerMacro extends CollectionPicklerUnpicklerMacro {
  import c.universe._
  import definitions._
  lazy val ConsClass = c.mirror.staticClass("scala.collection.immutable.$colon$colon")
  def mkType(eltpe: c.Type) = appliedType(ConsClass.toTypeConstructor, List(eltpe))
  def mkArray(picklee: c.Tree) = q"$picklee.toArray"
  def mkBuffer(eltpe: c.Type) = q"scala.collection.mutable.ListBuffer[$eltpe]()"
  def mkResult(buffer: c.Tree) = q"$buffer.toList"
}
