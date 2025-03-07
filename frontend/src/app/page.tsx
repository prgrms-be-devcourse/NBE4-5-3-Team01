import Link from "next/link";

export default function Home() {
  return (
    <>
      <h1 className="text-4xl font-bold text-center">
        Welcome to the Next.js Starter
      </h1>
      <div className="flex justify-center">
        <Link href="/notifications">
          <button className="bg-blue-500 hover:bg-blue-700 text-white font-bold py-2 px-4 rounded">
            Notifications
          </button>
        </Link>
      </div>
    </>
  );
}
